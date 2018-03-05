package xyz.tomclarke.fyp.gui.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.dao.SynLinkRepository;
import xyz.tomclarke.fyp.gui.model.SearchQuery;
import xyz.tomclarke.fyp.gui.model.SearchResult;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.PseudoPaper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Performs searches to find papers
 * 
 * @author tbc452
 *
 */
@Service
public class PaperSearch {

    private static final Logger log = LogManager.getLogger(PaperSearch.class);
    private static final int MAX_RESULTS_PER_PAGE = 15;
    private static final int SNIPPET_MAX_SIZE = 80;
    private static final int SNIPPET_EXTENSION = 50;

    @Autowired
    private PaperUtil util;
    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private HyponymRepository hypRepo;
    @Autowired
    private SynLinkRepository synLinkRepo;

    /**
     * Interpret a search query to find relevant papers
     * 
     * @param search
     *            The search information, supplied by the user
     * @return A list of relevant papers
     */
    public List<SearchResult> search(SearchQuery search) {
        // No search result, just do all
        if (search.getText() == null || search.getText().isEmpty()) {
            return buildResultList(paperRepo.findAll(), false, null);
        }

        // Get TD-IDF of the query
        String[] queryTokens = NlpUtil.getAllTokens(search.getText());
        Map<String, Double> queryValues = new HashMap<String, Double>();
        // Need a pseudo paper
        Paper queryPaper = new PseudoPaper(search.getText());
        for (String query : queryTokens) {
            queryValues.put(query, util.calculateTfIdf(query, queryPaper));
        }

        return buildResultList(searchByTokens(search, queryValues), true, search);
    }

    /**
     * Performs a more naive search for papers based on token occurrence
     * 
     * @param search
     *            The search information, supplied by the user
     * @param queryValues
     *            The query with TF-IDF values calculated
     * @return A list of papers in order to display to the user
     */
    private List<PaperDAO> searchByTokens(SearchQuery search, Map<String, Double> queryValues) {
        String regex = "";
        for (String key : queryValues.keySet()) {
            regex += key + "|";
        }

        // Remove the last |
        regex = regex.substring(0, regex.length() - 1);

        List<PaperDAO> matchingPapers = paperRepo.findByContentRegex(regex);

        // These papers have at least one of the terms in the query
        // Find their scores...
        Map<PaperDAO, Double> paperScores = new HashMap<PaperDAO, Double>();
        for (PaperDAO paper : matchingPapers) {
            double score = 0.0;
            // We're not going to worry about title or author as neither are included with
            // the current data set

            Map<String, String> relevantTerms = findQueryStringsInPaper(util.loadPaper(paper),
                    NlpUtil.getAllTokens(search.getText()));

            for (Entry<String, String> term : relevantTerms.entrySet()) {
                score += calculateScoreForTermInPaper(search, queryValues, paper, term.getKey(), term.getValue());
            }

            // Add it to the list
            paperScores.put(paper, score);
        }

        // Sort according to their score
        matchingPapers.sort(new Comparator<PaperDAO>() {
            @Override
            public int compare(PaperDAO p1, PaperDAO p2) {
                // This needs to be backwards (i.e. descending order) so the page displays it in
                // the correct order (you don't want to scroll to the bottom for the best
                // result)
                if (paperScores.get(p1) > paperScores.get(p2)) {
                    // Paper 1 is better than paper 2
                    return -1;
                } else if (paperScores.get(p1) < paperScores.get(p2)) {
                    // Paper 1 is worse than paper 2
                    return 1;
                } else {
                    // Equal (unlikely...)
                    return 0;
                }
            }
        });

        log.info("paper, score");
        for (PaperDAO paper : matchingPapers) {
            log.info(paper.getId() + ", " + paperScores.get(paper));
        }

        return matchingPapers;
    }

    /**
     * Calculates a score for a token in paper
     * 
     * @param search
     *            The original search
     * @param queryValues
     *            The relative values of the search tokens
     * @param paper
     *            The paper to create a score for
     * @param termInPaper
     *            The term in the paper the token is found from
     * @param tokenInSearch
     *            The token the term found is based upon
     * @return The score of the term/token in the paper
     */
    private double calculateScoreForTermInPaper(SearchQuery search, Map<String, Double> queryValues, PaperDAO paper,
            String termInPaper, String tokenInSearch) {
        // Add TF-IDF of token in paper * TF-IDF of query token
        double tfIdfWeighting = util.calculateTfIdf(termInPaper, util.loadPaper(paper))
                * queryValues.get(tokenInSearch);

        // Check if in key phrase
        // TODO convert this so it's one call, not the number of papers initially
        // returned
        List<KeyPhraseDAO> kps;
        if (!search.isFocusOnAny()) {
            // Don't care what classification
            kps = kpRepo.findByPaperAndText(paper, termInPaper);
        } else {
            kps = kpRepo.findByPaperAndTextAndClassification(paper, termInPaper, search.getFocusRegex());
        }

        double kpWeighting = 1.0;
        for (KeyPhraseDAO kp : kps) {
            if (kp.getText().toLowerCase().contains(termInPaper)) {
                // Currently doubling TF-IDF every time the key is used in a kp
                kpWeighting += 1.0;
            }
        }

        double additionalScore = kpWeighting * tfIdfWeighting;
        return additionalScore;
    }

    /**
     * Finds the strings that match to the query (full strings, not substrings if
     * the substrings are in the paper)
     * 
     * @param paper
     *            The parsed paper
     * @param tokens
     *            The search tokens
     * @return A map of (unique) relevant terms, and the query token the term came
     *         from
     */
    private Map<String, String> findQueryStringsInPaper(Paper paper, String[] tokens) {
        Map<String, String> relevantTerms = new HashMap<String, String>();
        String text = paper.getText().toLowerCase();
        for (String token : tokens) {
            if (text.contains(token)) {
                // Go through each instance of it
                for (int i = text.indexOf(token); i > -1; i = text.indexOf(token, i + 1)) {
                    // Either the start or the space before the word
                    int wordStart = Math.max(0, text.substring(0, i).lastIndexOf(" "));
                    // Either the full stop, the space after or just the length of the original word
                    int wordEnd = Math.max(i + token.length(),
                            i + Math.min(text.substring(i).indexOf(" "), text.substring(i).indexOf(".")));
                    String newTerm = text.substring(wordStart, wordEnd);

                    newTerm = NlpUtil.sanitiseString(newTerm).trim();
                    if (!relevantTerms.containsKey(newTerm)) {
                        relevantTerms.put(newTerm, token);
                    }
                }
            }
        }

        return relevantTerms;
    }

    /**
     * Convert a list of found papers into search results
     * 
     * @param papers
     *            The papers to display to the user
     * @param limitResults
     *            Whether or not to limit the returned results
     * @param search
     *            The search information, supplied by the user
     * @return The build results list
     */
    private List<SearchResult> buildResultList(Iterable<PaperDAO> papers, boolean limitResults, SearchQuery search) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        int index = 0;
        for (PaperDAO paper : papers) {
            if (limitResults) {
                if (index < MAX_RESULTS_PER_PAGE) {
                    index++;
                } else {
                    // We have MAX_RESULTS_PER_PAGE, send them back to the client
                    return results;
                }
            }
            SearchResult result = new SearchResult();
            result.setId(paper.getId());
            result.setPaper(paper.getTitle());
            if (paper.getTitle() == null || paper.getTitle().trim().isEmpty()) {
                result.setPaper(paper.getLocation());
            }
            result.setKps(kpRepo.countByPaper(paper));
            result.setRels(hypRepo.countByPaper(paper) + synLinkRepo.countByPaper(paper));

            // Try and extract a snippet
            if (search != null) {
                String[] searchTerms = NlpUtil.getAllTokens(search.getText());
                for (String term : searchTerms) {
                    String termReplaceRegex = "";
                    String caseInsensetiveRegex = "(?i)";
                    for (char c : term.toCharArray()) {
                        termReplaceRegex += caseInsensetiveRegex + c;
                    }

                    int tIndex = paper.getText().toLowerCase().indexOf(term);
                    if (tIndex > -1) {
                        String snippet = result.getSnippet();
                        // Is the term actually already there?
                        if (snippet != null && snippet.toLowerCase().contains(term)) {
                            result.setSnippet(snippet.replaceAll(termReplaceRegex, "<b>" + term + "</b>"));
                            continue;
                        }
                        // Ok, add it to the output
                        int sIndexStart = Math.max(0, tIndex - SNIPPET_EXTENSION);
                        sIndexStart = Math.max(0, paper.getText().substring(0, sIndexStart).lastIndexOf(" "));
                        int sIndexEnd = Math.min(paper.getText().length(), tIndex + SNIPPET_EXTENSION);
                        sIndexEnd = Math.max(sIndexEnd, paper.getText().substring(sIndexEnd).indexOf(" "));
                        if (snippet == null) {
                            snippet = "";
                        } else {
                            snippet += ", ";
                        }
                        snippet += paper.getText().substring(sIndexStart, sIndexEnd).replaceAll(termReplaceRegex,
                                "<b>" + term + "</b>") + "...";
                        result.setSnippet(snippet);
                    }
                }
            }

            // Make sure there's something to display
            if (result.getSnippet() == null || result.getSnippet().isEmpty()) {
                result.setSnippet(
                        paper.getText().substring(0,
                                Math.max(SNIPPET_MAX_SIZE,
                                        paper.getText().substring(SNIPPET_MAX_SIZE).indexOf(" ") + SNIPPET_MAX_SIZE))
                                + "...");
                // The indexOf + SNIPPET_MAX_SIZE will either be == or larger if the substring
                // is on a space or has a space, or 1 less as it could return -1
            }

            results.add(result);
        }
        return results;
    }

}
