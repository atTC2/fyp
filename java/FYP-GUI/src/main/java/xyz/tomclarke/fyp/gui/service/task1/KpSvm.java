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
import xyz.tomclarke.fyp.gui.dao.SynLinkDAO;
import xyz.tomclarke.fyp.gui.dao.SynLinkRepository;
import xyz.tomclarke.fyp.gui.dao.SynonymDAO;
import xyz.tomclarke.fyp.gui.dao.SynonymRepository;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.gui.service.PaperProcessor;
import xyz.tomclarke.fyp.gui.service.PaperUtil;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.Extraction;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;
import xyz.tomclarke.fyp.nlp.svm.KeyPhraseSVM;
import xyz.tomclarke.fyp.nlp.util.NlpObjectStore;

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
    private PaperUtil util;
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private SynLinkRepository synLinkRepo;
    @Autowired
    private SynonymRepository synRepo;
    private KeyPhraseSVM svm;

    @Override
    public void loadObjects() throws Exception {
        // Try and load the SVM
        svm = (KeyPhraseSVM) NlpObjectStore.loadNlpObj(KP_SVM);

        if (svm == null) {
            // Need to build the SVM and save it
            svm = new KeyPhraseSVM();
            svm.generateTrainingData(util.getTrainingPapers(), null, pp.getVec());
            svm.train();
            NlpObjectStore.saveNlpObj(KP_SVM, svm);
        }
    }

    @Override
    public boolean processPaper(PaperDAO paper) throws IOException {
        Paper paperForNlp = util.loadPaper(paper);
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

            // Get the quick wins (these should also have correct IDs)
            List<Extraction> exts = paperForNlp.makeEasyWinsFromKeyPhrases(null);
            for (Extraction ext : exts) {
                if (ext instanceof KeyPhrase) {
                    KeyPhrase kp = (KeyPhrase) ext;
                    boolean foundOriginalRecord = false;
                    // Try and update the original record (if the main kp)
                    for (KeyPhraseDAO kpDb : phrasesDb) {
                        if (kpDb.getRelativeId() == kp.getId()) {
                            // Update the record
                            foundOriginalRecord = true;
                            kpDb.setText(kp.getPhrase());
                            kpDb.setEnd(kp.getPosition().getEnd());
                            break;
                        }
                    }
                    // May need to make a new record for the acronym
                    if (!foundOriginalRecord) {
                        KeyPhraseDAO phraseDb = new KeyPhraseDAO();
                        phraseDb.setRelativeId(new Long(kp.getId()));
                        phraseDb.setPaper(paper);
                        phraseDb.setText(kp.getPhrase());
                        phraseDb.setStart(kp.getPosition().getStart());
                        phraseDb.setEnd(kp.getPosition().getEnd());
                        phrasesDb.add(phraseDb);
                    }
                }
            }
            // Save the key phrases (and extra bits)
            kpRepo.save(phrasesDb);

            // Handle relationships generated in easy win processing
            List<SynLinkDAO> synLinksToSave = new ArrayList<SynLinkDAO>();
            List<SynonymDAO> synsToSave = new ArrayList<SynonymDAO>();
            for (Extraction ext : exts) {
                if (ext instanceof Relationship) {
                    Relationship rel = (Relationship) ext;
                    // Add the relationship
                    SynLinkDAO link = new SynLinkDAO();
                    synLinksToSave.add(link);
                    for (KeyPhrase kp : rel.getPhrases()) {
                        SynonymDAO synDb = new SynonymDAO();
                        synDb.setSynLink(link);
                        // Find the KP
                        synDb.setSynLink(link);
                        boolean foundKP = false;
                        for (KeyPhraseDAO kpDb : phrasesDb) {
                            if (kpDb.getRelativeId() == kp.getId()) {
                                synDb.setKp(kpDb);
                                foundKP = true;
                                break;
                            }
                        }
                        if (foundKP) {
                            log.error("NO KP WAS FOUND");
                        }
                        synsToSave.add(synDb);
                    }
                }
            }
            // Save relationships
            synLinkRepo.save(synLinksToSave);
            synRepo.save(synsToSave);

            // Add the key phrases to the Paper object (will be saved upon method returning)
            paper.setParse(util.getPaperBytes(paperForNlp));

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
