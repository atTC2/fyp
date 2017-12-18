package xyz.tomclarke.fyp.nlp.cluster;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.TestOnPapers;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Tests clustering using Word2Vec
 * 
 * @author tbc452
 *
 */
public class TestW2VCluster extends TestOnPapers {

    private static final Logger log = LogManager.getLogger(TestW2VCluster.class);
    private static List<Paper> trainingPapers;

    @Ignore
    @Test
    public void testClusteringWithGoogleNews() throws Exception {
        log.info("Testing");
        W2VClusterProcessor cluster = new W2VClusterProcessor(
                Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS), trainingPapers);

        // There isn't really a relation between Google News and these training papers
        for (Paper testPaper : testPapers) {
            cluster.cluster(testPaper, Linkage.SINGLE);
            cluster.cluster(testPaper, Linkage.AVERAGE);
            cluster.cluster(testPaper, Linkage.COMPLETE);
            break;
        }

    }

    @Ignore
    @Test
    public void testClusteringWithWiki2Vec() throws Exception {
        log.info("Testing");
        W2VClusterProcessor cluster = new W2VClusterProcessor(
                Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.WIKI2VEC), trainingPapers);

        for (Paper testPaper : testPapers) {
            cluster.cluster(testPaper, Linkage.SINGLE);
            cluster.cluster(testPaper, Linkage.AVERAGE);
            cluster.cluster(testPaper, Linkage.COMPLETE);
            break;
        }

    }

    @Ignore
    @Test
    public void testClusteringWithScienceIE() throws Exception {
        log.info("Testing");
        W2VClusterProcessor cluster = new W2VClusterProcessor(Word2VecProcessor.generateFromPapers(trainingPapers),
                trainingPapers);

        for (Paper testPaper : testPapers) {
            cluster.cluster(testPaper, Linkage.SINGLE);
            cluster.cluster(testPaper, Linkage.AVERAGE);
            cluster.cluster(testPaper, Linkage.COMPLETE);
            break;
        }
    }

}
