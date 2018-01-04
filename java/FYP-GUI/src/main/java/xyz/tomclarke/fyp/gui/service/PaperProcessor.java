package xyz.tomclarke.fyp.gui.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.NlpObjectDAO;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.service.task0.PreProcessor;
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

    // Kinda looks sad without an annotation :'(
    private static final Logger log = LogManager.getLogger(PaperProcessor.class);
    @Autowired
    private PaperRepository paperRepo;
    @Autowired
    private PreProcessor task0;
    @Autowired
    private KpSvm task1;
    @Autowired
    private ClazzW2V task2;
    @Autowired
    private RelSvm task3;

    @Scheduled(fixedDelay = 10000)
    public void processWaitingPapers() throws Exception {
        processWaitingPapers(paperRepo.findByStatus(0), task0);
        processWaitingPapers(paperRepo.findByStatus(1), task1);
        processWaitingPapers(paperRepo.findByStatus(2), task2);
        processWaitingPapers(paperRepo.findByStatus(3), task3);
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
    private void processWaitingPapers(List<PaperDAO> papers, NlpProcessor nlp) throws Exception {
        if (!papers.isEmpty()) {
            log.info("Processing " + papers.size() + " papers with " + nlp.getClass().getName());
            // Ensure the required components are loaded
            nlp.loadObjects();
            // Process the papers
            for (PaperDAO paper : papers) {
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
    public static NlpObjectDAO buildNlpObj(String label, Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);

        NlpObjectDAO nlpObj = new NlpObjectDAO();
        nlpObj.setLabel(label);
        nlpObj.setData(baos.toByteArray());
        return nlpObj;
    }

    /**
     * Converts a loaded NLP object from bytes to the object
     * 
     * @param nlpObjectFromDB
     *            The object to convert back
     * @return The NLP Object to be saved in the database, or null if it couldn't be
     *         found or loaded
     */
    public static NlpProcessor returnNlpObj(NlpObjectDAO nlpObjectFromDB) {
        if (nlpObjectFromDB != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(nlpObjectFromDB.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (NlpProcessor) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error("Could not load object from database: " + nlpObjectFromDB.getLabel(), e);
                // It broke, so try making the object again as if it wasn't found
                return null;
            }
        } else {
            return null;
        }
    }

}
