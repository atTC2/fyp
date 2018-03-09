package xyz.tomclarke.fyp.gui.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;

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
            // Make sure the KPs are in starting index order
            kps.sort(new Comparator<KeyPhraseDAO>() {
                @Override
                public int compare(KeyPhraseDAO kp1, KeyPhraseDAO kp2) {
                    return kp1.getStart() - kp2.getStart();
                }
            });
            List<String> kpStrings = new ArrayList<String>();
            List<String> kpClazzs = new ArrayList<String>();
            int offset = 0;
            for (KeyPhraseDAO kp : kps) {
                kpStrings.add(kp.getText());
                kpClazzs.add(kp.getClassification());
                // Draw the key phrase on the text
                Pair<String, Integer> dec = decoratePaper(view.getText(), kp, offset);
                view.setText(dec.getFirst());
                offset = dec.getSecond();
            }
            view.setKps(kpStrings);
            view.setKpClazzs(kpClazzs);
        }

        mv.addObject("paper", view);
        return mv;
    }

    /**
     * Draws the key phrases onto the text sent to the user
     * 
     * @param text
     *            The original text (to be modified)
     * @param kp
     *            The key phrase object
     * @param offset
     *            The offset from the original text caused by other text decorations
     * @return The decorated original text
     */
    private Pair<String, Integer> decoratePaper(String text, KeyPhraseDAO kp, int offset) {
        final String labelMaterial = "<span class=\"label label-success\">";
        final String labelProcess = "<span class=\"label label-warning\">";
        final String labelTask = "<span class=\"label label-info\">";
        final String labelDefault = "<span class=\"label label-primary\">";
        final String labelEnd = "</span>";

        // Do it for every instance of the key phrase
        String before, segment, after;

        before = text.substring(0, kp.getStart() + offset);
        if (kp.getEnd() + offset < text.length()) {
            // KP is not the end of the document
            after = text.substring(kp.getEnd() + offset);
        } else {
            // KP is at end of document
            after = "";
        }

        Classification clazz = Classification.getClazz(kp.getClassification());
        switch (clazz) {
        case MATERIAL:
            segment = labelMaterial + kp.getText() + labelEnd;
            offset += labelMaterial.length() + labelEnd.length();
            break;
        case PROCESS:
            segment = labelProcess + kp.getText() + labelEnd;
            offset += labelProcess.length() + labelEnd.length();
            break;
        case TASK:
            segment = labelTask + kp.getText() + labelEnd;
            offset += labelTask.length() + labelEnd.length();
            break;
        default:
            segment = labelDefault + kp.getText() + labelEnd;
            offset += labelDefault.length() + labelEnd.length();
        }

        text = before + segment + after;

        return Pair.of(text, offset);
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
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + paper.getTitle() + ".ann\"");

        PrintWriter out = response.getWriter();

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
                if (currentId != syn.getSynLink().getId() && !synToSend.isEmpty()) {
                    out.println(synToSend);
                    synToSend = "";
                }

                if (synToSend.isEmpty()) {
                    synToSend = syn.toString();
                    currentId = syn.getSynLink().getId();
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
