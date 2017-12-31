package xyz.tomclarke.fyp.gui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Lists all papers and allows for searching
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/search")
public class Search {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("search");
        return mv;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView search() {
        ModelAndView mv = new ModelAndView("search");
        return mv;
    }

}
