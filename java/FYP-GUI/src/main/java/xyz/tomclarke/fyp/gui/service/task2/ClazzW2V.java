package xyz.tomclarke.fyp.gui.service.task2;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;
import xyz.tomclarke.fyp.nlp.word2vec.W2VClassifier;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

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
    private Word2Vec vec;

    @Override
    public void loadObjects() {
        vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
    }

    @Override
    public void processPaper(PaperDAO paper) {
        // Get all KPs for this paper
        List<KeyPhraseDAO> kps = kpRepo.findByPaper(paper);

        // Classify them
        for (KeyPhraseDAO kp : kps) {
            Classification clazz = W2VClassifier.getClazzBasedOnAvgDistance(kp.getText(), vec);
            kp.setClassification(clazz.toString());
            log.debug("KP Classification for KP ID " + kp.getId() + " was " + clazz);
        }

        // Save all of the changes
        kpRepo.save(kps);

        log.info("KP classification complete for " + kps.size() + " KPs");
    }

    @Override
    public void unload() {
        vec = null;
        System.gc();
    }

}
