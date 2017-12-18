package xyz.tomclarke.fyp.nlp;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.TestW2VClassifier;

/**
 * Loads training and test papers for use in testing algorithms
 * 
 * @author tbc452
 *
 */
public class TestOnPapers {

    private static final Logger log = LogManager.getLogger(TestOnPapers.class);

    public static List<Paper> trainingPapers;
    public static List<Paper> testPapers;

    @BeforeClass
    public static void initalise() {
        log.info("Loading training and test data...");
        trainingPapers = NlpUtil.loadAndAnnotatePapers(TestW2VClassifier.class);
        testPapers = NlpUtil.loadAndAnnotateTestPapers(TestW2VClassifier.class);
    }

}
