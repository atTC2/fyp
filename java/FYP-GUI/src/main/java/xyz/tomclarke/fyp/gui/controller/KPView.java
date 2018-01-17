package xyz.tomclarke.fyp.gui.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import xyz.tomclarke.fyp.gui.model.KPViewCloudEntity;
import xyz.tomclarke.fyp.gui.service.KeyPhraseCloudCache;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;

/**
 * Supplies information about key phrases
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/kps")
public class KPView {

    @Autowired
    private KeyPhraseCloudCache cloudCache;

    /**
     * Get all key phrases and pre-process so they can be visualised in a word cloud
     * 
     * @return The model and view
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewAll() {
        ModelAndView mv = new ModelAndView("kps");
        return mv;
    }

    /**
     * Creates a JSON array of cloud word entities
     * 
     * @param The
     *            type of key phrase to generate a cloud for
     * @return The list of key phrase entities for cloud visualisation
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public List<KPViewCloudEntity> getCloudData(@RequestParam("type") String type)
            throws ClassNotFoundException, IOException {
        if (Classification.getClazz(type).equals(Classification.TASK)) {
            return cloudCache.getTaskCloud();
        } else if (Classification.getClazz(type).equals(Classification.PROCESS)) {
            return cloudCache.getProcessCloud();
        } else if (Classification.getClazz(type).equals(Classification.MATERIAL)) {
            return cloudCache.getMaterialCloud();
        } else {
            return null;
        }
    }

    /**
     * View a single type of key phrases in table form
     * 
     * @param type
     *            The type of key phrases to find
     * @return The model and view
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{type}")
    public ModelAndView viewOne(@PathVariable("type") String type) {
        ModelAndView mv = new ModelAndView("kp");

        if (type.toLowerCase().matches("tasks?")) {
            mv.addObject("title", Classification.TASK.toString());
            mv.addObject("kps", cloudCache.getTaskRows());
        } else if (type.toLowerCase().matches("process(es)?")) {
            mv.addObject("title", Classification.PROCESS.toString());
            mv.addObject("kps", cloudCache.getProcessRows());
        } else if (type.toLowerCase().matches("materials?")) {
            mv.addObject("title", Classification.MATERIAL.toString());
            mv.addObject("kps", cloudCache.getMaterialRows());
        } else {
            mv.addObject("title", "Unknown Key Phrase Type");
        }

        return mv;
    }
}
