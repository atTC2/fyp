package xyz.tomclarke.fyp.gui.service.task1;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.NlpObjectRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
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

    private static final Logger log = LogManager.getLogger(KpSvm.class);
    private static final String KP_SVM = "KP_EXTRACTION_SVM";
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private NlpObjectRepository nlpObjectRepo;
    private Word2Vec vec;
    private KeyPhraseSVM svm;

    @Override
    public void loadObjects() throws Exception {
        vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);

        // Try and load the SVM
        if (nlpObjectRepo.countByLabel(KP_SVM) == 1) {
            svm = (KeyPhraseSVM) PaperProcessor.loadNlpObj(nlpObjectRepo.findByLabel(KP_SVM));
        }

        if (svm == null) {
            // Need to build the SVM and save it
            List<Paper> trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class, true);
            svm = new KeyPhraseSVM();
            svm.generateTrainingData(trainingPapers, null, vec);
            svm.train();
            nlpObjectRepo.save(PaperProcessor.buildNlpObj(KP_SVM, svm));
        }
    }

    @Override
    public void processPaper(PaperDAO paper) {
        Paper paperForNlp = PaperProcessor.loadPaper(paper);
        if (paperForNlp != null) {
            // Do some processing
            List<KeyPhrase> phrases = svm.predictKeyPhrases(paperForNlp, vec);
            List<KeyPhraseDAO> phrasesDb = new ArrayList<KeyPhraseDAO>();
            // Convert the phrases into database format
            for (int i = 0; i < phrases.size(); i++) {
                KeyPhrase phrase = phrases.get(i);
                KeyPhraseDAO phraseDb = new KeyPhraseDAO();

                phraseDb.setRelativeId(Long.valueOf(i));
                phraseDb.setPaper(paper);
                phraseDb.setText(phrase.getPhrase());
                phraseDb.setStart(phrase.getPosition().getStart());
                phraseDb.setEnd(phrase.getPosition().getEnd());

                phrasesDb.add(phraseDb);
            }

            // Save the key phrases
            kpRepo.save(phrasesDb);

            log.info("KP extraction complete for Paper ID " + paper.getId() + " found " + phrases.size() + " KPs");
        }
    }

    @Override
    public void unload() {
        vec = null;
        svm = null;
        System.gc();
    }

}
