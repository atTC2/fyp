package xyz.tomclarke.fyp.nlp.word2vec;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.TestOnPapers;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.svm.TestKeyPhraseSVM;

/**
 * Tests W2V classifiers
 * 
 * @author tbc452
 *
 */
public class TestW2VClassifier extends TestOnPapers {

    private static final Logger log = LogManager.getLogger(TestKeyPhraseSVM.class);

    @Test
    public void testGetClazzGN() throws Exception {
        testAllScenarios(Word2VecPretrained.GOOGLE_NEWS);
    }

    @Test
    public void testGetClazzFI() throws Exception {
        testAllScenarios(Word2VecPretrained.FREEBASE_IDS);
    }

    /**
     * Runs all test to demonstrate Word2Vec development
     * 
     * @param set
     *            The pre-trained Word2Vec model
     */
    private void testAllScenarios(Word2VecPretrained set) {
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(set);

        log.info("distance metric, autoClazz, removeStopWords, useManyWords, correct, total, percentage");
        testGetClazzBasedOnAvgDistance(vec, true, Classification.UNKNOWN, false, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.UNKNOWN, false, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.UNKNOWN, true, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.UNKNOWN, false, true);
        testGetClazzBasedOnAvgDistance(vec, true, Classification.UNKNOWN, true, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.UNKNOWN, true, true);
        testGetClazzBasedOnAvgDistance(vec, true, Classification.UNKNOWN, false, true);
        testGetClazzBasedOnAvgDistance(vec, true, Classification.MATERIAL, false, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.MATERIAL, false, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.MATERIAL, true, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.MATERIAL, false, true);
        testGetClazzBasedOnAvgDistance(vec, true, Classification.MATERIAL, true, false);
        testGetClazzBasedOnAvgDistance(vec, false, Classification.MATERIAL, true, true);
        testGetClazzBasedOnAvgDistance(vec, true, Classification.MATERIAL, false, true);
    }

    /**
     * Test simple Word2Vec classification on existing KeyPhrases
     * 
     * @param vec
     *            The pre-trained Word2Vec
     * @param closest
     *            If true uses closest distance, if false uses average
     * @param autoClazz
     *            The default classification to assign if none can be selected
     * @param removeStopWords
     *            Whether to remove stop words from classification
     * @param useManyWords
     *            Whether to use just the classification word or many (hopefully
     *            similar) words
     */
    private void testGetClazzBasedOnAvgDistance(Word2Vec vec, boolean closest, Classification autoClazz,
            boolean removeStopWords, boolean useManyWords) {
        int correct = 0;
        int total = 0;
        for (Paper paper : testPapers) {
            List<KeyPhrase> kps = paper.getKeyPhrases();

            for (KeyPhrase kp : kps) {
                Classification predClazz;
                if (closest) {
                    predClazz = W2VClassifier.getClazzBasedOnClosestDistance(kp.getPhrase(), vec, autoClazz,
                            removeStopWords, useManyWords);
                } else {
                    predClazz = W2VClassifier.getClazzBasedOnAvgDistance(kp.getPhrase(), vec, autoClazz,
                            removeStopWords, useManyWords);
                }
                if (predClazz.equals(kp.getClazz())) {
                    correct++;
                }
                total++;
                // So the others are just 0? :P
            }
        }

        // Build output
        String output = "";
        if (closest) {
            output += "closest";
        } else {
            output += "average";
        }
        output += ", " + autoClazz.toString();
        if (removeStopWords) {
            output += ", yes";
        } else {
            output += ", noo";
        }
        if (useManyWords) {
            output += ", yes";
        } else {
            output += ", noo";
        }

        output += ", " + correct + ", " + total + ", " + ((double) correct / (double) total * 100.0) + "%";

        log.info(output);
    }

}
