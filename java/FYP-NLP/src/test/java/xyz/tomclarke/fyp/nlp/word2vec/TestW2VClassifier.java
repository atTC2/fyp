package xyz.tomclarke.fyp.nlp.word2vec;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.TestOnPapers;
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistic;
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

        // Average distance
        log.info("W2V Classification - based on average distance using " + set + " with default clazz "
                + Classification.UNKNOWN + " and stopwords " + false);
        testGetClazzBasedOnAvgDistance(vec, Classification.UNKNOWN, false);
        log.info("W2V Classification - based on average distance using " + set + " with default clazz "
                + Classification.MATERIAL + " and stopwords " + false);
        testGetClazzBasedOnAvgDistance(vec, Classification.MATERIAL, false);
        log.info("W2V Classification - based on average distance using " + set + " with default clazz "
                + Classification.MATERIAL + " and stopwords " + true);
        testGetClazzBasedOnAvgDistance(vec, Classification.MATERIAL, true);

        // Closest distance
        log.info("W2V Classification - based on closest distance using " + set + " with default clazz "
                + Classification.UNKNOWN + " and stopwords " + false);
        testGetClazzBasedOnClosestDistance(vec, Classification.UNKNOWN, false);
        log.info("W2V Classification - based on closest distance using " + set + " with default clazz "
                + Classification.MATERIAL + " and stopwords " + false);
        testGetClazzBasedOnClosestDistance(vec, Classification.MATERIAL, false);
        log.info("W2V Classification - based on closest distance using " + set + " with default clazz "
                + Classification.MATERIAL + " and stopwords " + true);
        testGetClazzBasedOnClosestDistance(vec, Classification.MATERIAL, true);
    }

    /**
     * Test simple Word2Vec classification on existing KeyPhrases
     * 
     * @param vec
     *            The pre-trained Word2Vec
     * @param autoClazz
     *            The default classification to assign if none can be selected
     * @param removeStopWords
     *            Whether to remove stop words from classification
     */
    private void testGetClazzBasedOnAvgDistance(Word2Vec vec, Classification autoClazz, boolean removeStopWords) {
        List<ConfusionStatistic> overallStats = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            int tp = 0;
            int fp = 0;
            int tn = 0;
            int fn = 0;

            List<KeyPhrase> kps = paper.getKeyPhrases();

            for (KeyPhrase kp : kps) {
                Classification predClazz = W2VClassifier.getClazzBasedOnAvgDistance(kp.getPhrase(), vec, autoClazz,
                        removeStopWords);
                if (predClazz.equals(kp.getClazz())) {
                    tp++;
                } else {
                    fp++;
                }
                // So the others are just 0? :P
            }

            overallStats.add(ConfusionStatistic.calculateScore(tp, fp, tn, fn));
        }

        ConfusionStatistic stats = ConfusionStatistic.calculateScoreSum(overallStats);

        log.info("Overall statistics: " + stats);
        log.info("Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());
    }

    /**
     * Test simple Word2Vec classification on existing KeyPhrases
     * 
     * @param vec
     *            The pre-trained Word2Vec
     * @param autoClazz
     *            The default classification to assign if none can be selected
     * @param removeStopWords
     *            Whether to remove stop words from classification
     */
    private void testGetClazzBasedOnClosestDistance(Word2Vec vec, Classification autoClazz, boolean removeStopWords) {
        List<ConfusionStatistic> overallStats = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            int tp = 0;
            int fp = 0;
            int tn = 0;
            int fn = 0;

            List<KeyPhrase> kps = paper.getKeyPhrases();

            for (KeyPhrase kp : kps) {
                Classification predClazz = W2VClassifier.getClazzBasedOnClosestDistance(kp.getPhrase(), vec, autoClazz,
                        removeStopWords);
                if (predClazz.equals(kp.getClazz())) {
                    tp++;
                } else {
                    fp++;
                }
                // So the others are just 0? :P
            }

            overallStats.add(ConfusionStatistic.calculateScore(tp, fp, tn, fn));
        }

        ConfusionStatistic stats = ConfusionStatistic.calculateScoreSum(overallStats);

        log.info("Overall statistics: " + stats);
        log.info("Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());
    }

}
