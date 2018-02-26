package xyz.tomclarke.fyp.gui.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.PaperRepository;
import xyz.tomclarke.fyp.gui.service.task0.PreProcessor;
import xyz.tomclarke.fyp.gui.service.task1.KpSvm;
import xyz.tomclarke.fyp.gui.service.task2.ClazzW2V;
import xyz.tomclarke.fyp.gui.service.task3.RelSvm;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

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
    private static final Word2VecPretrained vecModel = Word2VecPretrained.GOOGLE_NEWS;
    @Autowired
    private KeyPhraseCloudCache cloudCache;
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
    private Word2Vec vec;
    private List<Paper> trainingPapers;

    /**
     * Loads training papers
     * 
     * @throws IOException
     */
    @PostConstruct
    public void initialise() throws IOException {
        trainingPapers = NlpUtil.loadAndAnnotatePapers(true);
    }

    @Scheduled(fixedDelay = 60000)
    public void processWaitingPapers() throws Exception {
        // Call each of the tasks (including pre-processing) with their corresponding
        // status ID
        processWaitingPapers(0, task0);
        processWaitingPapers(1, task1);
        processWaitingPapers(2, task2);
        processWaitingPapers(3, task3);
    }

    /**
     * Loads the Word2Vec model
     */
    public void loadVec() {
        if (vec == null) {
            // Load the Word2Vec model
            vec = Word2VecProcessor.loadPreTrainedData(vecModel);
        }
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
        // Be ready to update the cache systems if anything actually happens
        boolean oneSuccessfullyProcessed = false;
        if (!papers.isEmpty()) {
            // Only bother loading this if we actually need it
            loadVec();

            log.info("Processing " + papers.size() + " papers with " + nlp.getClass().getName());
            // Ensure the required components are loaded
            nlp.loadObjects();
            // Process the papers
            for (PaperDAO paper : papers) {
                try {
                    if (nlp.processPaper(paper)) {
                        paper.setStatus(status + 1);
                        oneSuccessfullyProcessed = true;
                    } else {
                        paper.setStatus(Long.valueOf(paperFailStatus));
                    }
                } catch (Exception e) {
                    log.error("Problem processing paper ID " + paper.getId(), e);
                    paper.setStatus(Long.valueOf(paperFailStatus));
                } finally {
                    // Causes more updates than if just waiting until the end, but stops repeat
                    // processing if it crashes half way through
                    paperRepo.save(paper);
                }
            }
            // Unload components to help with memory
            nlp.unload();
            log.info("Finished processing " + papers.size() + " papers with " + nlp.getClass().getName());

        }
        // Update cache systems
        if (oneSuccessfullyProcessed) {
            cloudCache.updateCache();
        }
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

    /**
     * Converts a paper object to bytes for saving in the database
     * 
     * @param paper
     *            The paper to convert
     * @return The bytes
     * @throws IOException
     */
    public byte[] getPaperBytes(Paper paper) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(paper);
        return baos.toByteArray();
    }

    /**
     * Gets the Word2Vec instance to use
     * 
     * @return A Word2Vec model
     */
    public Word2Vec getVec() {
        return vec;
    }

    /**
     * Sets the Word2Vec model (potentially to null to help save memory)
     * 
     * @param vec
     */
    public void setVec(Word2Vec vec) {
        this.vec = vec;
    }

    /**
     * Gets the training papers
     * 
     * @return
     */
    public List<Paper> getTrainingPapers() {
        return trainingPapers;
    }

    /**
     * Sets the training papers
     * 
     * @param trainingPapers
     */
    public void setTrainingPapers(List<Paper> trainingPapers) {
        this.trainingPapers = trainingPapers;
    }

}
