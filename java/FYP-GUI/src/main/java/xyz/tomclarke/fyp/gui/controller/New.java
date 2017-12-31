package xyz.tomclarke.fyp.gui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Allows for adding new papers for analysis and listing
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/new")
public class New {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("new");
        return mv;
    }

}
