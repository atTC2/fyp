package xyz.tomclarke.fyp.gui.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.service.task0.PreProcessor;
import xyz.tomclarke.fyp.gui.service.task1.KpSvm;
import xyz.tomclarke.fyp.gui.service.task2.ClazzW2V;
import xyz.tomclarke.fyp.gui.service.task3.RelSvm;
import xyz.tomclarke.fyp.nlp.paper.Paper;

/**
 * Processes papers. It repeatedly gets papers to do processing on
 * 
 * @author tbc452
 *
 */
@Service
public class PaperProcessor {

    private static final Logger log = LogManager.getLogger(PaperProcessor.class);
    private static final Long paperFailStatus = Long.valueOf(-1);
    @Value("${nlp.objectpath}")
    private String nlpObjectPath;
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
        // TODO If this runs out of memory, try having the methods returning a boolean
        // saying if it had work to do and if it did, skip the other steps for now and
        // hopefully when it next runs the memory will have clearer
        processWaitingPapers(0, task0);
        processWaitingPapers(1, task1);
        processWaitingPapers(2, task2);
        processWaitingPapers(3, task3);
    }

    /**
     * Processes a list of papers for a given NLP task
     * 
     * @param status
     *            The status papers need to be in to be processed
     * @param nlp
     *            The task processor
     * @throws Exception
     */
    private void processWaitingPapers(long status, NlpProcessor nlp) throws Exception {
        List<PaperDAO> papers = paperRepo.findByStatus(status);
        if (!papers.isEmpty()) {
            log.info("Processing " + papers.size() + " papers with " + nlp.getClass().getName());
            // Ensure the required components are loaded
            nlp.loadObjects();
            // Process the papers
            for (PaperDAO paper : papers) {
                try {
                    if (nlp.processPaper(paper)) {
                        paper.setStatus(status + 1);
                    } else {
                        paper.setStatus(Long.valueOf(paperFailStatus));
                    }
                } catch (Exception e) {
                    log.error("Problem processing paper ID " + paper.getId(), e);
                    paper.setStatus(Long.valueOf(paperFailStatus));
                }
            }
            paperRepo.save(papers);
            // Unload components to help with memory
            nlp.unload();
            log.info("Finished processing " + papers.size() + " papers with " + nlp.getClass().getName());
        }
    }

    /**
     * Saves an NLP object to the database
     * 
     * @param label
     *            The label to identify the NLP object
     * @param obj
     *            The object to save
     * @throws IOException
     */
    public void saveNlpObj(String label, Object obj) throws IOException {
        // Get rid of the old file if it's there
        File objFile = getFileFromLabel(label);
        if (objFile.exists()) {
            objFile.delete();
        }

        // Write object to disk
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(objFile));
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * Finds if an NLP object with a given label exists on disk
     * 
     * @param label
     *            The label identifying the NLP object
     * @return Whether it is found on disk and loading can be attempted
     */
    public boolean checkIfCanLoadNlpObj(String label) {
        return getFileFromLabel(label).exists();
    }

    /**
     * Converts a loaded NLP object from bytes to the object
     * 
     * @param label
     *            The label of the object to load
     * @return The NLP Object to be saved, or null if it couldn't be found or loaded
     */
    public NlpProcessor loadNlpObj(String label) {
        File objFile = getFileFromLabel(label);

        // Check it exists
        if (!objFile.exists()) {
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile));
            NlpProcessor nlpObj = (NlpProcessor) ois.readObject();
            ois.close();
            return nlpObj;
        } catch (ClassNotFoundException | IOException e) {
            log.error("Could not load NLP object: " + label, e);
            // It broke, so try making the object again as if it wasn't found
            return null;
        }
    }

    /**
     * Finds the full path to a NLP object with the label specified
     * 
     * @param label
     *            The label of the NLP object
     * @return The full path name
     */
    private File getFileFromLabel(String label) {
        return new File(nlpObjectPath + label + ".ser");
    }

    /**
     * Converts a loaded Paper object to the actual object
     * 
     * @param paperFromDb
     *            The object to convert back
     * @return The Paper object loaded from the database
     */
    public Paper loadPaper(PaperDAO paperFromDb) {
        if (paperFromDb != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(paperFromDb.getParse());
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (Paper) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error("Could not load Paper object from database, ID " + paperFromDb.getId(), e);
                // It broke, so try making the object again as if it wasn't found
                return null;
            }
        } else {
            return null;
        }
    }
}
