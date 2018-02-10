package xyz.tomclarke.fyp.gui.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import xyz.tomclarke.fyp.gui.model.SearchQuery;
import xyz.tomclarke.fyp.gui.model.SearchResult;
import xyz.tomclarke.fyp.gui.service.PaperSearch;

/**
 * Lists all papers and allows for searching
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/search")
public class Search {

    private static final Logger log = LogManager.getLogger(Search.class);

    @Autowired
    private PaperSearch paperSearch;

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

        // Do the search
        List<SearchResult> searchResults = paperSearch.search(search);
        mv.addObject("results", searchResults);

        // Record what was searched, may be interesting
        log.info(search + ", FOUND:" + searchResults.size());

        // Let the user know nothing was found
        if (searchResults.isEmpty()) {
            mv.addObject("noResults", true);
        }

        return mv;
    }

}
