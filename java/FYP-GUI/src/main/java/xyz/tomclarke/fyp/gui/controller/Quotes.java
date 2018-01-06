package xyz.tomclarke.fyp.gui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Something a little fun...
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/quotes")
public class Quotes {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("quotes");
        return mv;
    }
}
