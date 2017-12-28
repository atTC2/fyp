package xyz.tomclarke.fyp.gui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Acts as the home page for the GUI
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/")
public class Home {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("home");
        mv.addObject("message", "TESTING, attention please.");
        return mv;
    }

}
