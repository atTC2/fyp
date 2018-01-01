package xyz.tomclarke.fyp.gui.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import xyz.tomclarke.fyp.gui.dao.KeyPhrase;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.Paper;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.dao.Synonym;
import xyz.tomclarke.fyp.gui.dao.SynonymRepository;

/**
 * Allows viewing and actions on a paper
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/view")
public class View {

    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private KeyPhraseRepository kpRepo;
    // @Autowired
    // private HyponymRepository hypRepo;
    @Autowired
    private SynonymRepository synRepo;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(@RequestParam("paper") Long paperId) {
        ModelAndView mv = new ModelAndView("view");
        boolean validPaper = false;

        xyz.tomclarke.fyp.gui.dao.Paper paper = paperRepo.findOne(paperId);
        if (paper != null) {
            validPaper = true;
            mv.addObject("paper", paper);
            List<KeyPhrase> kps = kpRepo.findByPaper(paper);
            mv.addObject("kps", kps);
            // List<Hyponym> hyps = hypRepo.findByKp(kps);
            // mv.addObject("hyps", hyps);
            List<Synonym> syns = synRepo.findByKp(kps);
            mv.addObject("syns", syns);
        }
        mv.addObject("id", paperId);
        mv.addObject("validPaper", validPaper);
        mv.addObject("title", validPaper ? paper.getTitle() : "Paper not found");

        return mv;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam("paper") Long paperId, HttpServletResponse response) throws IOException {
        // Get the paper information
        Paper paper = paperRepo.findOne(paperId);

        // Check there is a paper
        if (paper == null) {
            redirectOnDownloadFail(paperId, response);
            return;
        }

        // Send information
        File paperFile = new File(paper.getLocation());
        if (paperFile.exists()) {
            // File exists, send it
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + paper.getTitle() + "."
                    + FilenameUtils.getExtension(paper.getLocation()) + "\"");
            IOUtils.copy(new ByteArrayInputStream(FileUtils.readFileToByteArray(paperFile)),
                    response.getOutputStream());
        } else if (paper.getText() != null && !paper.getText().isEmpty()) {
            // Got the text, let's try and send that
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + paper.getTitle() + ".txt\"");
            IOUtils.copy(new ByteArrayInputStream(paper.getText().getBytes()), response.getOutputStream());
        } else {
            // Nothing to download
            redirectOnDownloadFail(paperId, response);
            return;
        }
        response.flushBuffer();
    }

    @RequestMapping(value = "/extractions", method = RequestMethod.GET)
    public void extractions(@RequestParam("paper") Long paperId, HttpServletResponse response) throws IOException {
        // Get the paper information
        Paper paper = paperRepo.findOne(paperId);

        // Check there is a paper
        if (paper == null) {
            redirectOnDownloadFail(paperId, response);
            return;
        }

        List<KeyPhrase> kps = kpRepo.findByPaper(paper);
        // List<Hyponym> hyps = hypRepo.findByKp(kps);
        List<Synonym> syns = synRepo.findByKp(kps);

        // Send information
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + paper.getTitle() + ".ann\"");

        ServletOutputStream out = response.getOutputStream();

        for (KeyPhrase kp : kps) {
            out.println(kp.toString());
        }
        // for (Hyponym hyp : hyps) {
        // out.println(hyp.toString());
        // }
        String synToSend = "";
        Long currentId = 0L;
        for (Synonym syn : syns) {
            // If a new ID, write info and clear data
            if (currentId != syn.getId() && !synToSend.isEmpty()) {
                out.println(synToSend);
                synToSend = "";
            }

            if (synToSend.isEmpty()) {
                synToSend = syn.toString();
                currentId = syn.getId();
            } else {
                synToSend += " " + syn.getId();
            }
        }
        // Write any remaining data
        if (!synToSend.isEmpty()) {
            out.println(synToSend);
            synToSend = "";
        }

        out.flush();
        response.flushBuffer();
    }

    /**
     * Redirects the user in the event the download fails
     * 
     * @param paperId
     *            The ID of the paper attempting to be achieved
     * @param response
     *            The HTTP response to fill out
     * @throws IOException
     */
    private void redirectOnDownloadFail(Long paperId, HttpServletResponse response) throws IOException {
        response.sendRedirect("/view?paper=" + paperId);
    }

}
