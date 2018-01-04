package xyz.tomclarke.fyp.gui.service.task1;

import java.util.List;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.NlpObjectRepository;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.gui.service.PaperProcessor;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.svm.KeyPhraseSVM;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Processes papers and finds key phrases
 * 
 * @author tbc452
 *
 */
@Component
public class KpSvm implements NlpProcessor {

    private static final String KP_SVM = "KP_EXTRACTION_SVM";
    @Autowired
    private NlpObjectRepository nlpObjectRepo;
    private Word2Vec vec;
    private KeyPhraseSVM svm;

    @Override
    public void loadObjects() throws Exception {
        vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);

        // Try and load the SVM
        if (nlpObjectRepo.countByLabel(KP_SVM) == 1) {
            svm = (KeyPhraseSVM) PaperProcessor.returnNlpObj(nlpObjectRepo.findByLabel(KP_SVM));
        }

        if (svm == null) {
            // Need to build the SVM and save it
            List<Paper> trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class);
            svm = new KeyPhraseSVM();
            svm.generateTrainingData(trainingPapers, null, vec);
            svm.train();
            nlpObjectRepo.save(PaperProcessor.buildNlpObj(KP_SVM, svm));
        }
    }

    @Override
    public void processPaper(PaperDAO paper) {
        Paper paperForNlp = null;
        List<KeyPhrase> phrases = svm.predictKeyPhrases(paperForNlp, vec);
    }

    @Override
    public void unload() {
        vec = null;
        svm = null;
        System.gc();
    }

}
