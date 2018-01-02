package xyz.tomclarke.fyp.gui.service.task3;

import java.util.List;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.NlpObjectRepository;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.svm.RelationshipSVM;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Process papers to find relationship information
 * 
 * @author tbc452
 *
 */
@Component
public class RelSvm implements NlpProcessor {

    @Autowired
    private NlpObjectRepository nlpObjectRepo;

    @Override
    public void loadObjects() throws Exception {
        List<Paper> trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class);
        Word2Vec vecForSvm = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        Annotator ann = new Annotator();

        // Hyponym
        RelationshipSVM svmHyp = new RelationshipSVM();
        svmHyp.generateTrainingData(trainingPapers, RelationType.HYPONYM_OF, vecForSvm, ann);
        svmHyp.train();

        // Synonym
        RelationshipSVM svmSyn = new RelationshipSVM();
        svmSyn.generateTrainingData(trainingPapers, RelationType.SYNONYM_OF, vecForSvm, ann);
        svmSyn.train();
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
