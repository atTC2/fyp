package xyz.tomclarke.fyp.gui.service.task2;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.HyponymDAO;
import xyz.tomclarke.fyp.gui.dao.HyponymRepository;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseDAO;
import xyz.tomclarke.fyp.gui.dao.KeyPhraseRepository;
import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.dao.SynonymDAO;
import xyz.tomclarke.fyp.gui.dao.SynonymRepository;
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
    private PaperProcessor pp;
    @Autowired
    private KeyPhraseRepository kpRepo;
    @Autowired
    private HyponymRepository hypRepo;
    @Autowired
    private SynonymRepository synRepo;

    @Override
    public void loadObjects() {
        // Nothing to do here
    }

    @Override
    public boolean processPaper(PaperDAO paper) {
        // Get all KPs for this paper
        // Because of the paper state, we can assume non of the key phrases for the
        // paper have already had classification applied to them
        List<KeyPhraseDAO> kps = kpRepo.findByPaper(paper);
        log.info("KP classification on paper ID " + paper.getId() + " for " + kps.size() + " KPs");

        // Classify them
        for (KeyPhraseDAO kp : kps) {
            if (kp.getClassification() == null || kp.getClassification().isEmpty()) {
                Classification clazz = W2VClassifier.getClazzBasedOnAvgDistance(kp.getText(), pp.getVec(),
                        Classification.MATERIAL, true, false);
                // This doesn't set the information on the extractions object in the actual
                // Paper object, but that's ok for now...

                // Handle hyponyms and synonyms (propagate clazz)
                List<KeyPhraseDAO> relatedKps = new ArrayList<KeyPhraseDAO>();
                List<HyponymDAO> hyps = hypRepo.findByKp(kp);
                for (HyponymDAO hyp : hyps) {
                    if (!hyp.getKp1().equals(kp)) {
                        relatedKps.add(findKpFromList(hyp.getKp1(), kps));
                    }
                    if (!hyp.getKp2().equals(kp)) {
                        relatedKps.add(findKpFromList(hyp.getKp2(), kps));
                    }
                }
                List<SynonymDAO> syns = synRepo.findRelatedByKp(kp);
                for (SynonymDAO syn : syns) {
                    relatedKps.add(findKpFromList(syn.getKp(), kps));
                }
                // No related KP will have a clazz already (if they have already been processed,
                // this KP would already be processing, meaning if this somehow happens the
                // logic is terribly broken)

                // Find the strongest value
                // For now base off of key phrase length
                for (KeyPhraseDAO relKp : relatedKps) {
                    Classification relClazz = W2VClassifier.getClazzBasedOnAvgDistance(relKp.getText(), pp.getVec(),
                            Classification.MATERIAL, true, false);
                    if (!relClazz.equals(clazz)) {
                        // Pick the largest
                        if (kp.getText().length() < relKp.getText().length()) {
                            clazz = relClazz;
                        }
                    }
                }

                // Set values
                kp.setClassification(clazz.toString());
                for (KeyPhraseDAO relKp : relatedKps) {
                    relKp.setClassification(clazz.toString());
                }
            }
        }

        // Save all of the changes
        kpRepo.save(kps);

        log.info("KP classification completed for paper ID " + paper.getId() + " for " + kps.size() + " KPs");
        return true;
    }

    /**
     * Find a KP in a list of KPs
     * 
     * @param kpToFind
     *            The KP (already loaded) to find
     * @param kps
     *            The list of KPs
     * @return The KP object to work on
     */
    private KeyPhraseDAO findKpFromList(KeyPhraseDAO kpToFind, List<KeyPhraseDAO> kps) {
        for (KeyPhraseDAO kp : kps) {
            if (kp.equals(kpToFind)) {
                return kp;
            }
        }
        log.error("Could not find KP in list " + kpToFind.getId() + " in paper " + kpToFind.getPaper().getId());
        return null;
    }

    @Override
    public void unload() {
        // Nothing to do
        System.gc();
    }

}
