package xyz.tomclarke.fyp.nlp.cluster;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Tests clustering using Word2Vec
 * 
 * @author tbc452
 *
 */
public class TestW2VCluster {

    private static final Logger log = LogManager.getLogger(TestW2VCluster.class);
    private static List<Paper> trainingPapers;
    private static List<Paper> testPapers;

    @BeforeClass
    public static void initalise() {
        log.info("Loading training and test data...");
        trainingPapers = NlpUtil.loadAndAnnotatePapers(TestW2VCluster.class);
        testPapers = NlpUtil.loadAndAnnotateTestPapers(TestW2VCluster.class);
    }

    @Test
    public void testSomething() throws Exception {
        log.info("Testing");
        W2VClusterProcessor cluster = new W2VClusterProcessor(Word2VecProcessor.loadGoogleNewsVectors(),
                trainingPapers);

        for (Paper testPaper : testPapers) {
            cluster.cluster(testPaper, Linkage.SINGLE);
            cluster.cluster(testPaper, Linkage.AVERAGE);
            cluster.cluster(testPaper, Linkage.COMPLETE);
            break;
        }

    }

}
