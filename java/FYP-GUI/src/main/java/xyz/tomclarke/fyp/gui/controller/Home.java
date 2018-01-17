package xyz.tomclarke.fyp.gui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.dao.SynLinkRepository;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;

/**
 * Acts as the home page for the GUI
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/")
public class Home {

    @Autowired
    private PaperRepository paper;
    @Autowired
    private KeyPhraseRepository kp;
    @Autowired
    private HyponymRepository hyp;
    @Autowired
    private SynLinkRepository syn;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("home");
        mv.addObject("countPaper", paper.count());
        mv.addObject("countKP", kp.count());
        mv.addObject("countHyp", hyp.count());
        mv.addObject("countSyn", syn.count());
        mv.addObject("countTask", kp.countByClassification(Classification.TASK.toString()));
        mv.addObject("countProcess", kp.countByClassification(Classification.PROCESS.toString()));
        mv.addObject("countMaterial", kp.countByClassification(Classification.MATERIAL.toString()));
        return mv;
    }

}
