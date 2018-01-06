package xyz.tomclarke.fyp.gui.service.task2;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.gui.service.PaperProcessor;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;
import xyz.tomclarke.fyp.nlp.word2vec.W2VClassifier;

/**
 * Processes papers and classifies key phrases
 * 
 * @author tbc452
 *
 */
@Component
public class ClazzW2V implements NlpProcessor {

    private static final Logger log = LogManager.getLogger(ClazzW2V.class);
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private PaperProcessor pp;

    @Override
    public void loadObjects() {
        // Nothing to do here
    }

    @Override
    public boolean processPaper(PaperDAO paper) {
        // Get all KPs for this paper
        List<KeyPhraseDAO> kps = kpRepo.findByPaper(paper);
        log.info("KP classification on paper ID " + paper.getId() + " for " + kps.size() + " KPs");

        // Classify them
        for (KeyPhraseDAO kp : kps) {
            Classification clazz = W2VClassifier.getClazzBasedOnAvgDistance(kp.getText(), pp.getVec());
            kp.setClassification(clazz.toString());
            // This doesn't set the information on the extractions object in the actual
            // Paper object, but that's ok for now...
        }

        // Save all of the changes
        kpRepo.save(kps);

        log.info("KP classification completed for paper ID " + paper.getId() + " for " + kps.size() + " KPs");
        return true;
    }

    @Override
    public void unload() {
        // Nothing to do
        System.gc();
    }

}
