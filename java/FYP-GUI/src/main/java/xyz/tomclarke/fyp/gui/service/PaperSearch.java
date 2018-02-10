package xyz.tomclarke.fyp.gui.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.dao.SynLinkRepository;
import xyz.tomclarke.fyp.gui.model.SearchQuery;
import xyz.tomclarke.fyp.gui.model.SearchResult;

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

    /**
     * Interpret a search query to find relevant papers
     * 
     * @param search
     *            The search information, supplied by the user
     * @return A list of relevant papers
     */
    public List<SearchResult> search(SearchQuery search) {
        List<SearchResult> results = new ArrayList<SearchResult>();

        // TODO Do actual search at some point

        Iterable<PaperDAO> papers = paperRepo.findAll();
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
