package xyz.tomclarke.fyp.gui.service.task1;

import java.io.IOException;
import java.util.List;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.NlpObjectRepository;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.nlp.paper.Paper;
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

    @Override
    public void loadObjects() throws Exception {
        List<Paper> trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class);
        Word2Vec vecForSvm = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        KeyPhraseSVM svm = new KeyPhraseSVM(vecForSvm);
        svm.generateTrainingData(trainingPapers, null);
        svm.train();
    }

    @Override
    public void processPaper(xyz.tomclarke.fyp.gui.dao.Paper paper) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unload() {
        // TODO Auto-generated method stub

    }

}
