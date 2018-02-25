package xyz.tomclarke.fyp.gui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
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

    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private HyponymRepository hypRepo;
    @Autowired
    private SynLinkRepository synLinkRepo;
    private List<Paper> trainingPapers;

    /**
     * Load the necessary resources to run the search
     * 
     * @throws IOException
     */
    @PostConstruct
    public void initialise() throws IOException {
        trainingPapers = NlpUtil.loadAndAnnotatePapers(true);
    }

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
            return buildResultList(paperRepo.findAll());
        }

        // Get TD-IDF of the query
        String[] queryTokens = NlpUtil.getAllTokens(search.getText());
        Map<String, Double> queryValues = new HashMap<String, Double>();
        // Need a pseudo paper
        Paper queryPaper = new PseudoPaper(search.getText());
        for (String query : queryTokens) {
            queryValues.put(query, NlpUtil.calculateTfIdf(query, queryPaper, trainingPapers));
        }

        return buildResultList(searchByTokens(queryValues));
    }

    /**
     * Performs a more naive search for papers based on token occurrence
     * 
     * @param queryValues
     *            The query with TF-IDF values calculated
     * @return A list of papers in order to display to the user
     */
    private List<PaperDAO> searchByTokens(Map<String, Double> queryValues) {
        String regex = "";
        for (String key : queryValues.keySet()) {
            regex += key + "|";
        }
        // Remove the last |
        regex = regex.substring(0, regex.length() - 2);

        return paperRepo.findByTextWithRegex(regex);
    }

    /**
     * Search for papers using latent semantic analysis
     * 
     * @param queryValues
     *            The query with TF-IDF values calculated
     * @return A list of papers in order to display to the user
     */
    @SuppressWarnings("unused")
    private List<PaperDAO> searchUsingLsa(Map<String, Double> queryValues) {
        return null;
    }

    /**
     * Convert a list of found papers into search results
     * 
     * @param papers
     *            The papers to display to the user
     * @return The build results list
     */
    private List<SearchResult> buildResultList(Iterable<PaperDAO> papers) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        for (PaperDAO paper : papers) {
            SearchResult result = new SearchResult();
            result.setId(paper.getId());
            result.setPaper(paper.getTitle());
            if (paper.getTitle() == null || paper.getTitle().trim().isEmpty()) {
                result.setPaper(paper.getLocation());
            }
            result.setKps(kpRepo.countByPaper(paper));
            result.setRels(hypRepo.countByPaper(paper) + synLinkRepo.countByPaper(paper));
            results.add(result);
        }
        return results;
    }

}
