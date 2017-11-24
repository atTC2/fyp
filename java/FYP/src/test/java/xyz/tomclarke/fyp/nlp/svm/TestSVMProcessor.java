package xyz.tomclarke.fyp.nlp.svm;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import libsvm.svm_node;
import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistic;
import xyz.tomclarke.fyp.nlp.evaluation.EvaluateExtractions;
import xyz.tomclarke.fyp.nlp.evaluation.Strictness;
import xyz.tomclarke.fyp.nlp.keyphrase.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Test to evaluate the SVM Processor
 * 
 * @author tbc452
 *
 */
public class TestSVMProcessor {

    private static final Logger log = LogManager.getLogger(TestSVMProcessor.class);

    private static SVMProcessor svm;
    private static List<Paper> testPapers;

    @BeforeClass
    public static void initalise() throws Exception {
        log.info("Loading training and test data...");
        List<Paper> papers = NlpUtil.loadAndAnnotatePapers(TestSVMProcessor.class);
        testPapers = NlpUtil.loadAndAnnotateTestPapers(TestSVMProcessor.class);

        // Setup the SVM
        log.info("Building SVM...");
        svm = new SVMProcessor();
        svm.generateTrainingData(papers);
        svm.train();

        log.info("SVM trained, now to test...");
    }

    @Ignore
    @Test
    public void testSvmProcessorSameData() throws Exception {
        log.info("Testing with training data");
        // Test on input data...
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;
        svm_problem problem = svm.getProblem();
        for (int i = 0; i < problem.l; i++) {
            boolean isPredictedKeyPhrase = svm.predictIsKeyword(problem.x[i]);
            if (problem.y[i] == 0.0 && isPredictedKeyPhrase) {
                fp++;
            } else if (problem.y[i] == 0.0 && !isPredictedKeyPhrase) {
                tn++;
            } else if (problem.y[i] == 1.0 && isPredictedKeyPhrase) {
                tp++;
            } else if (problem.y[i] == 1.0 && !isPredictedKeyPhrase) {
                fn++;
            } else {
                throw new Exception("Problem label not understood: " + problem.y[i]);
            }
        }

        log.info(String.format("tp: %.8f fp: %.8f tn: %.8f fn: %.8f", tp, fp, tn, fn));
        log.info(ConfusionStatistic.calculateScore(tp, fp, tn, fn));
    }

    @Ignore
    @Test
    public void testSvmProcessorTestData() throws Exception {
        log.info("Testing with test data");
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;
        for (Paper paper : testPapers) {
            boolean previousWordKeyPhrase = false;
            for (CoreMap sentence : paper.getAnnotations()) {
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    // Key phrase (answer)
                    double keyPhrase = paper.isTokenPartOfKeyPhrase(token) ? 1.0 : 0.0;

                    // SV Nodes (question)
                    svm_node[] nodes = svm.generateSupportVectors(token, paper, previousWordKeyPhrase ? 1.0 : 0.0);

                    // Ask the question and compare the answer to the expected answer
                    boolean isPredictedKeyPhrase = svm.predictIsKeyword(nodes);
                    if (keyPhrase == 0.0 && isPredictedKeyPhrase) {
                        fp++;
                    } else if (keyPhrase == 0.0 && !isPredictedKeyPhrase) {
                        tn++;
                    } else if (keyPhrase == 1.0 && isPredictedKeyPhrase) {
                        tp++;
                    } else if (keyPhrase == 1.0 && !isPredictedKeyPhrase) {
                        fn++;
                    }

                    // Save previous word information (for next word in same paper only)
                    previousWordKeyPhrase = isPredictedKeyPhrase;
                }
            }
        }

        log.info(String.format("tp: %.8f fp: %.8f tn: %.8f fn: %.8f", tp, fp, tn, fn));
        log.info(ConfusionStatistic.calculateScore(tp, fp, tn, fn));
    }

    @Test
    public void testSvmPredictKeyPhrases() {
        log.info("Testing with test data, getting key phrase objects");
        List<ConfusionStatistic> overallStatsGen = new ArrayList<ConfusionStatistic>();
        List<ConfusionStatistic> overallStatsInc = new ArrayList<ConfusionStatistic>();
        List<ConfusionStatistic> overallStatsStr = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            List<KeyPhrase> phrases = svm.predictKeyPhrases(paper);

            // Save statistics
            overallStatsGen.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getExtractions(),
                    Strictness.GENEROUS, false));
            overallStatsInc.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getExtractions(),
                    Strictness.INCLUSIVE, false));
            overallStatsStr.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getExtractions(),
                    Strictness.STRICT, false));
        }

        ConfusionStatistic gen = ConfusionStatistic.calculateScoreSum(overallStatsGen);
        ConfusionStatistic inc = ConfusionStatistic.calculateScoreSum(overallStatsInc);
        ConfusionStatistic str = ConfusionStatistic.calculateScoreSum(overallStatsStr);

        log.info("Overall statistics (gen): " + gen);
        log.debug("Specific results were: tp: " + gen.getTp() + " fp: " + gen.getFp() + " tn: " + gen.getTn() + " fn: "
                + gen.getFn());
        log.info("Overall statistics (inc): " + inc);
        log.debug("Specific results were: tp: " + inc.getTp() + " fp: " + inc.getFp() + " tn: " + inc.getTn() + " fn: "
                + inc.getFn());
        log.info("Overall statistics (str): " + str);
        log.debug("Specific results were: tp: " + str.getTp() + " fp: " + str.getFp() + " tn: " + str.getTn() + " fn: "
                + str.getFn());
    }

    @Ignore
    @Test
    public void testSvmPredictSaveToFile() throws IOException {
        String saveLocation = System.getenv("FYP_HOME") + "../testing/pred/";
        for (Paper paper : testPapers) {
            List<KeyPhrase> phrases = svm.predictKeyPhrases(paper);

            // Write extractions to file
            try (PrintWriter pw = new PrintWriter(new FileWriter(saveLocation))) {
                for (KeyPhrase phrase : phrases) {
                    pw.write(phrase.toString());
                }
            }
        }
    }

}
