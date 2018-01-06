package xyz.tomclarke.fyp.gui.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.model.PaperLocation;

/**
 * Allows for adding new papers for analysis and listing
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/add")
public class Add {

    private static final Logger log = LogManager.getLogger(Add.class);
    @Autowired
    private PaperRepository paperRepo;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("add");
        mv.addObject("location", new PaperLocation());
        mv.addObject("hasErrored", false);
        mv.addObject("hasSucceeded", false);
        return mv;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView submit(@Valid @ModelAttribute("location") PaperLocation location, BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView("add");

        if (bindingResult.hasErrors()) {
            mv.addObject("hasErrored", true);
            return mv;
        }
        mv.addObject("hasErrored", false);
        mv.addObject("hasSucceeded", true);

        try {
            PaperDAO paper = new PaperDAO();
            paper.setLocation(location.getLocation());
            paper.setStatus(Long.valueOf(0));
            paperRepo.save(paper);

            log.info("Added paper via GUI with location " + location.getLocation());
            mv.addObject("message", "Paper at <code>" + location.getLocation() + "</code> added to the database.");
            mv.addObject("paperId", paper.getId());

            return mv;
        } catch (Exception e) {
            log.info("Problem adding paper via GUI with location " + location.getLocation(), e);
            mv.addObject("hasErrored", true);
            mv.addObject("hasSucceeded", false);
            bindingResult.addError(
                    new FieldError("location", "location", "There was a problem adding the paper to the database"));
            return mv;
        }
    }

}
