package xyz.tomclarke.fyp.gui.service.task1;

import java.io.IOException;
import java.util.ArrayList;
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
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.svm.KeyPhraseSVM;
import xyz.tomclarke.fyp.nlp.util.NlpObjectStore;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

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
    private PaperProcessor pp;
    @Autowired
    private KeyPhraseRepository kpRepo;
    private KeyPhraseSVM svm;

    @Override
    public void loadObjects() throws Exception {
        // Try and load the SVM
        svm = (KeyPhraseSVM) NlpObjectStore.loadNlpObj(KP_SVM);

        if (svm == null) {
            // Need to build the SVM and save it
            List<Paper> trainingPapers = NlpUtil.loadAndAnnotatePapers(NlpUtil.class, true);
            svm = new KeyPhraseSVM();
            svm.generateTrainingData(trainingPapers, null, pp.getVec());
            svm.train();
            NlpObjectStore.saveNlpObj(KP_SVM, svm);
        }
    }

    @Override
    public boolean processPaper(PaperDAO paper) throws IOException {
        Paper paperForNlp = pp.loadPaper(paper);
        log.info("KP extraction on Paper ID " + paper.getId());
        if (paperForNlp != null) {
            // Do some processing
            List<KeyPhrase> phrases = svm.predictKeyPhrases(paperForNlp, pp.getVec());
            List<KeyPhraseDAO> phrasesDb = new ArrayList<KeyPhraseDAO>();
            // Convert the phrases into database format
            for (int i = 0; i < phrases.size(); i++) {
                KeyPhrase phrase = phrases.get(i);
                KeyPhraseDAO phraseDb = new KeyPhraseDAO();

                phraseDb.setRelativeId(Long.valueOf(i + 1));
                phrase.setId(phraseDb.getRelativeId().intValue());
                phraseDb.setPaper(paper);
                phraseDb.setText(phrase.getPhrase());
                phraseDb.setStart(phrase.getPosition().getStart());
                phraseDb.setEnd(phrase.getPosition().getEnd());

                phrasesDb.add(phraseDb);
                paperForNlp.addExtraction(phrase);
            }

            // Save the key phrases
            kpRepo.save(phrasesDb);

            // Add the key phrases to the Paper object (will be saved upon method returning)
            paper.setParse(pp.getPaperBytes(paperForNlp));

            log.info("KP extraction complete for Paper ID " + paper.getId() + " found " + phrases.size() + " KPs");
            return true;
        }
        return false;
    }

    @Override
    public void unload() {
        svm = null;
        System.gc();
    }

}
