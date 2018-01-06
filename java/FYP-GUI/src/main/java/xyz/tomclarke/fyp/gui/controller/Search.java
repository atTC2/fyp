package xyz.tomclarke.fyp.gui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.model.SearchQuery;
import xyz.tomclarke.fyp.gui.model.SearchResult;

/**
 * Lists all papers and allows for searching
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/search")
public class Search {

    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private KeyPhraseRepository kpRepo;
    // @Autowired
    // private HyponymRepository hypRepo;
    // @Autowired
    // private SynonymRepository synRepo;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("search");
        mv.addObject("search", new SearchQuery());
        return mv;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView search(@Valid @ModelAttribute("search") SearchQuery search, BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView("search");

        if (bindingResult.hasErrors()) {
            mv.addObject("hasErrored", true);
            mv.addObject("search", search);
            return mv;
        }

        // Do actual search at some point

        Iterable<PaperDAO> papers = paperRepo.findAll();
        List<SearchResult> results = new ArrayList<SearchResult>();
        for (PaperDAO paper : papers) {
            SearchResult result = new SearchResult();
            result.setId(paper.getId());
            result.setPaper(paper.getTitle());
            if (paper.getTitle() == null || paper.getTitle().trim().isEmpty()) {
                result.setPaper(paper.getLocation());
            }
            result.setKps(kpRepo.countByPaper(paper));
            results.add(result);
        }
        mv.addObject("results", results);

        return mv;
    }

}
