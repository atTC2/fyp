package xyz.tomclarke.fyp.gui.service;

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
     */
    @PostConstruct
    public void initialise() {
        trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class, true);
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

        // LSA

        // Get TD-IDF of the query
        String[] queryTokens = NlpUtil.getAllTokens(search.getText());
        Map<String, Double> queryValues = new HashMap<String, Double>();
        // Need a pseudo paper
        Paper queryPaper = new PseudoPaper(search.getText());
        for (String query : queryTokens) {
            queryValues.put(query, NlpUtil.calculateTfIdf(query, queryPaper, trainingPapers));
        }

        // TODO find some papers
        return buildResultList(paperRepo.findAll());
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
