package xyz.tomclarke.fyp.gui.service.task3;

import java.util.List;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.NlpObjectRepository;
import xyz.tomclarke.fyp.gui.dao.SynonymRepository;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.gui.service.PaperProcessor;
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
 * This class doesn't strictly follow the interface as to fit the whole thing in
 * to memory is very difficult, so `loadObjects()` is used to verify that the
 * SVMs are prepared and that the Word2Vec and Annotator are loaded.
 * `processPaper()` will need to clean some memory and load the SVMs when need
 * be
 * 
 * @author tbc452
 *
 */
@Component
public class RelSvm implements NlpProcessor {

    private static final String REL_SVM_HYP = "REL_SVM_HYP";
    private static final String REL_SVM_SYN = "REL_SVM_SYN";
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private HyponymRepository hypRepo;
    @Autowired
    private SynonymRepository synRepo;
    @Autowired
    private NlpObjectRepository nlpObjectRepo;

    @Override
    public void loadObjects() throws Exception {
        boolean hypAvailable = nlpObjectRepo.countByLabel(REL_SVM_HYP) == 1;
        boolean synAvailable = nlpObjectRepo.countByLabel(REL_SVM_SYN) == 1;

        // See if we have both available
        if (!(hypAvailable && synAvailable)) {
            List<Paper> trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class);
            Word2Vec vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
            Annotator ann = new Annotator();

            // Build the SVM data
            RelationshipSVM hyp = new RelationshipSVM();
            if (!hypAvailable) {
                hyp.generateTrainingData(trainingPapers, RelationType.HYPONYM_OF, vec, ann);
            }
            RelationshipSVM syn = new RelationshipSVM();
            if (!synAvailable) {
                syn.generateTrainingData(trainingPapers, RelationType.SYNONYM_OF, vec, ann);
            }

            // Train the SVMs, one at a time and clear the memory
            vec = null;
            ann = null;
            System.gc();

            if (!hypAvailable) {
                hyp.train();
                nlpObjectRepo.save(PaperProcessor.buildNlpObj(REL_SVM_HYP, hyp));
            }
            hyp = null;
            System.gc();

            if (!synAvailable) {
                syn.train();
                nlpObjectRepo.save(PaperProcessor.buildNlpObj(REL_SVM_SYN, syn));
            }
        }
    }

    @Override
    public void processPaper(PaperDAO paper) {
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        Annotator ann = new Annotator();
    }

    @Override
    public void unload() {
        // Nothing to do here
    }

}
