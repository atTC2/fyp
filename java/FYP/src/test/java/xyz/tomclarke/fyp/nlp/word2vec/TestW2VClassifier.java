package xyz.tomclarke.fyp.nlp.word2vec;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Ignore;
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
    public void testGetClazzBasedOnAvgDistanceGN() throws Exception {
        testGetClazzBasedOnAvgDistance(Word2VecPretrained.GOOGLE_NEWS);
    }

    @Ignore
    @Test
    public void testGetClazzBasedOnAvgDistanceW2V() throws Exception {
        // Wiki2Vec doesn't work right now
        testGetClazzBasedOnAvgDistance(Word2VecPretrained.WIKI2VEC);
    }

    @Ignore
    @Test
    public void testGetClazzBasedOnAvgDistanceFI() throws Exception {
        testGetClazzBasedOnAvgDistance(Word2VecPretrained.FREEBASE_IDS);
    }

    @Ignore
    @Test
    public void testGetClazzBasedOnAvgDistanceFN() throws Exception {
        testGetClazzBasedOnAvgDistance(Word2VecPretrained.FREEBASE_NAMES);
    }

    /**
     * Test simple Word2Vec classification on existing KeyPhrases
     * 
     * @param set
     *            The pre-trained Word2Vec model
     */
    private void testGetClazzBasedOnAvgDistance(Word2VecPretrained set) {
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(set);
        List<ConfusionStatistic> overallStats = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            int tp = 0;
            int fp = 0;
            int tn = 0;
            int fn = 0;

            List<KeyPhrase> kps = paper.getKeyPhrases();

            for (KeyPhrase kp : kps) {
                Classification predClazz = W2VClassifier.getClazzBasedOnAvgDistance(kp.getPhrase(), vec);
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

        log.info("W2V Classification - based on average distance using " + set);
        log.info("Overall statistics: " + stats);
        log.info("Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());
    }

    @Test
    public void testGetClazzBasedOnClosestDistanceGN() throws Exception {
        testGetClazzBasedOnClosestDistance(Word2VecPretrained.GOOGLE_NEWS);
    }

    @Ignore
    @Test
    public void testGetClazzBasedOnClosestDistanceW2V() throws Exception {
        // Wiki2Vec doesn't work right now
        testGetClazzBasedOnClosestDistance(Word2VecPretrained.WIKI2VEC);
    }

    @Ignore
    @Test
    public void testGetClazzBasedOnClosestDistanceFI() throws Exception {
        testGetClazzBasedOnClosestDistance(Word2VecPretrained.FREEBASE_IDS);
    }

    @Ignore
    @Test
    public void testGetClazzBasedOnClosestDistanceFN() throws Exception {
        testGetClazzBasedOnClosestDistance(Word2VecPretrained.FREEBASE_NAMES);
    }

    /**
     * Test simple Word2Vec classification on existing KeyPhrases
     * 
     * @param set
     *            The pre-trained Word2Vec model
     */
    private void testGetClazzBasedOnClosestDistance(Word2VecPretrained set) {
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(set);
        List<ConfusionStatistic> overallStats = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            int tp = 0;
            int fp = 0;
            int tn = 0;
            int fn = 0;

            List<KeyPhrase> kps = paper.getKeyPhrases();

            for (KeyPhrase kp : kps) {
                Classification predClazz = W2VClassifier.getClazzBasedOnClosestDistance(kp.getPhrase(), vec);
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

        log.info("W2V Classification - based on closest distance using " + set);
        log.info("Overall statistics: " + stats);
        log.info("Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());
    }

}
