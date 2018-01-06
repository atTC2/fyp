package xyz.tomclarke.fyp.gui.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import xyz.tomclarke.fyp.gui.dao.HyponymDAO;
import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.dao.SynonymDAO;
import xyz.tomclarke.fyp.gui.dao.SynonymRepository;
import xyz.tomclarke.fyp.gui.model.PaperView;

/**
 * Allows viewing and actions on a paper
 * 
 * @author tbc452
 *
 */
@Controller
@RequestMapping(value = "/view")
public class View {

    private static final Long STATUS_MAX = Long.valueOf(4);
    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private HyponymRepository hypRepo;
    @Autowired
    private SynonymRepository synRepo;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(@RequestParam(value = "paper", required = false) Long paperId) {
        if (paperId == null) {
            // No paper, just send the user back to search
            return new ModelAndView("redirect:/search");
        }

        ModelAndView mv = new ModelAndView("view");
        PaperView view = new PaperView();

        view.setValidPaper(false);
        view.setTitle("Paper not found");
        view.setId(paperId);

        // Get the paper information
        PaperDAO paper = paperRepo.findOne(paperId);
        if (paper != null) {
            view.setValidPaper(true);
            view.setTitle(paper.getTitle());
            view.setAuthor(paper.getAuthor());
            view.setText(paper.getText());
            view.setSuccessful(paper.getStatus() == STATUS_MAX);
            view.setFailure(paper.getStatus() == Long.valueOf(-1));
            if (view.isSuccessful() || view.isFailure()) {
                view.setProgress("100%");
            } else {
                Double percentage = Double.valueOf(paper.getStatus()) / Double.valueOf(STATUS_MAX);
                view.setProgress((percentage * 100) + "%");
            }

            List<KeyPhraseDAO> kps = kpRepo.findByPaper(paper);
            List<String> kpStrings = new ArrayList<String>();
            List<String> kpClazzs = new ArrayList<String>();
            for (KeyPhraseDAO kp : kps) {
                kpStrings.add(kp.getText());
                kpClazzs.add(kp.getClassification());
            }
            view.setKps(kpStrings);
            view.setKpClazzs(kpClazzs);

            // TODO include relationships in view
            if (kps != null && !kps.isEmpty()) {
                List<HyponymDAO> hyps = hypRepo.findByKpIn(kps);
                mv.addObject("hyps", hyps);
                List<SynonymDAO> syns = synRepo.findByKpIn(kps);
                mv.addObject("syns", syns);
            }
        }

        mv.addObject("paper", view);
        return mv;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam("paper") Long paperId, HttpServletResponse response) throws IOException {
        // Get the paper information
        PaperDAO paper = paperRepo.findOne(paperId);

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
        PaperDAO paper = paperRepo.findOne(paperId);

        // Check there is a paper
        if (paper == null) {
            redirectOnDownloadFail(paperId, response);
            return;
        }

        List<KeyPhraseDAO> kps = kpRepo.findByPaper(paper);
        List<HyponymDAO> hyps = null;
        List<SynonymDAO> syns = null;
        if (kps != null && !kps.isEmpty()) {
            hyps = hypRepo.findByKpIn(kps);
            syns = synRepo.findByKpIn(kps);
        }

        // Send information
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + paper.getTitle() + ".ann\"");

        ServletOutputStream out = response.getOutputStream();

        for (KeyPhraseDAO kp : kps) {
            out.println(kp.toString());
        }
        if (hyps != null && !hyps.isEmpty()) {
            for (HyponymDAO hyp : hyps) {
                out.println(hyp.toString());
            }
        }
        if (syns != null && !syns.isEmpty()) {
            String synToSend = "";
            Long currentId = 0L;
            for (SynonymDAO syn : syns) {
                // If a new ID, write info and clear data
                if (currentId != syn.getId() && !synToSend.isEmpty()) {
                    out.println(synToSend);
                    synToSend = "";
                }

                if (synToSend.isEmpty()) {
                    synToSend = syn.toString();
                    currentId = syn.getId();
                } else {
                    synToSend += " T" + syn.getKp().getRelativeId();
                }
            }
            // Write any remaining data
            if (!synToSend.isEmpty()) {
                out.println(synToSend);
            }
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
