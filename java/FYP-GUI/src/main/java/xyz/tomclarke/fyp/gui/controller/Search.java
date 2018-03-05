package xyz.tomclarke.fyp.gui.controller;

import java.util.concurrent.ExecutionException;

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
import xyz.tomclarke.fyp.gui.model.SearchResultAndDetails;
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
    public ModelAndView search(@Valid @ModelAttribute("search") SearchQuery search, BindingResult bindingResult)
            throws InterruptedException, ExecutionException {
        ModelAndView mv = new ModelAndView("search");

        if (bindingResult.hasErrors()) {
            mv.addObject("hasErrored", true);
            mv.addObject("search", search);
            return mv;
        }

        // Do the search
        SearchResultAndDetails searchRAndD = paperSearch.search(search);
        mv.addObject("results", searchRAndD.getResults());

        // Record what was searched
        log.info(search + ", FOUND:" + searchRAndD.getResultsFound() + ", TOOK:" + searchRAndD.getSearchTime() + "ms");

        String endPartOfSearchResultString = searchRAndD.getResultsFound() > searchRAndD.getResults().size()
                ? " (showing top " + searchRAndD.getResults().size() + " results)."
                : ".";
        mv.addObject("resultsInfo",
                "Search for \"" + search.getText() + "\" completed in "
                        + ((double) searchRAndD.getSearchTime() / 1000.0) + " seconds, finding "
                        + searchRAndD.getResultsFound() + " papers" + endPartOfSearchResultString);

        // Let the user know nothing was found
        if (searchRAndD.getResults().isEmpty()) {
            mv.addObject("noResults", true);
        }

        return mv;
    }

}
