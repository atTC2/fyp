package xyz.tomclarke.fyp.gui.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.NlpObject;
import xyz.tomclarke.fyp.gui.dao.Paper;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.service.task1.KpSvm;
import xyz.tomclarke.fyp.gui.service.task2.ClazzW2V;
import xyz.tomclarke.fyp.gui.service.task3.RelSvm;

/**
 * Processes papers
 * 
 * @author tbc452
 *
 */
@Component
public class PaperProcessor {

    // Kind looks sad without an annotation :'(
    private static final Logger log = LogManager.getLogger(PaperProcessor.class);
    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private KpSvm task1;
    @Autowired
    private ClazzW2V task2;
    @Autowired
    private RelSvm task3;

    @Scheduled(fixedDelay = 10000)
    public void processWaitingPapers() throws Exception {
        processWaitingPapers(paperRepo.findByStatus(0), task1);
        processWaitingPapers(paperRepo.findByStatus(1), task2);
        processWaitingPapers(paperRepo.findByStatus(2), task3);
    }

    /**
     * Processes a list of papers for a given NLP task
     * 
     * @param papers
     *            The papers to work on
     * @param nlp
     *            The task processor
     * @throws Exception
     */
    private void processWaitingPapers(List<Paper> papers, NlpProcessor nlp) throws Exception {
        if (!papers.isEmpty()) {
            log.info("Processing " + papers.size() + " papers with " + nlp.getClass().getName());
            // Ensure the required components are loaded
            nlp.loadObjects();
            // Process the papers
            for (Paper paper : papers) {
                nlp.processPaper(paper);
            }
            // Unload components to help with memory
            nlp.unload();
        }
    }

    /**
     * Saves an NLP object to the database
     * 
     * @param label
     *            The label to identify the NLP object
     * @param obj
     *            The object to save
     * @return The NLP Object to be saved in the database
     * @throws IOException
     */
    public static NlpObject buildNlpObj(String label, Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);

        NlpObject nlpObj = new NlpObject();
        nlpObj.setLabel(label);
        nlpObj.setData(baos.toByteArray());
        return nlpObj;
    }

}
