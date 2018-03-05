package xyz.tomclarke.fyp.gui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.model.SearchQuery;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Calculates scores for papers
 * 
 * @author tbc452
 *
 */
@Service
public class PaperScoreCalculator {

    @Autowired
    private PaperUtil util;

    /**
     * Calculates the score for a paper. This is an asynchronous method.
     * 
     * This asynchronicity significantly improves performance: "a" ~80 secs -> ~23
     * secs; "the" ~10 secs -> ~3.8 secs; "support vector machine" ~0.5 secs -> ~0.2
     * secs
     * 
     * @param paper
     *            The paper in question
     * @param search
     *            The original user search
     * @param queryValues
     *            The search token values
     * @param matchingKps
     *            KPs which match the search
     * @return The score of the paper
     */
    @Async
    public CompletableFuture<Double> calculatePaperScore(PaperDAO paper, SearchQuery search,
            Map<String, Double> queryValues, List<KeyPhraseDAO> matchingKps) {
        double score = 0.0;

        // We're not going to worry about title or author as neither are included with
        // the current data set

        Map<String, String> relevantTerms = findQueryStringsInPaper(util.loadPaper(paper),
                NlpUtil.getAllTokens(search.getText()));

        for (Entry<String, String> term : relevantTerms.entrySet()) {
            score += calculatePaperScoreForTerm(search, queryValues, paper, term.getKey(), term.getValue(),
                    matchingKps);
        }
        return CompletableFuture.completedFuture(score);
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
     * @param allMatchingKps
     *            Key phrases that match the search
     * @return The score of the term/token in the paper
     */
    private double calculatePaperScoreForTerm(SearchQuery search, Map<String, Double> queryValues, PaperDAO paper,
            String termInPaper, String tokenInSearch, List<KeyPhraseDAO> allMatchingKps) {
        // Add TF-IDF of token in paper * TF-IDF of query token
        double tfIdfWeighting = util.calculateTfIdf(termInPaper, util.loadPaper(paper))
                * queryValues.get(tokenInSearch);

        // Get all relevant key phrases
        List<KeyPhraseDAO> kps = new ArrayList<KeyPhraseDAO>();
        for (KeyPhraseDAO kp : allMatchingKps) {
            if (kp.getPaper().getId().equals(paper.getId())) {
                kps.add(kp);
            }
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
}
