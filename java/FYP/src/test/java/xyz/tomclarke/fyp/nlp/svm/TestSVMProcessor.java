package xyz.tomclarke.fyp.nlp.svm;

import java.io.File;
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
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistics;
import xyz.tomclarke.fyp.nlp.evaluation.EvaluateExtractions;
import xyz.tomclarke.fyp.nlp.evaluation.Strictness;
import xyz.tomclarke.fyp.nlp.keyphrase.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.PaperUtil;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;

/**
 * Test to evaluate the SVM Processor
 * 
 * @author tbc452
 *
 */
public class TestSVMProcessor {

    private static final Logger log = LogManager.getLogger(TestSVMProcessor.class);

    private static SVMProcessor svm;

    @BeforeClass
    public static void initalise() throws Exception {
        log.info("Loading training data...");
        List<Paper> papers = PaperUtil.annotatePapers(LoadPapers
                .loadNewPapers(new File(TestSVMProcessor.class.getClassLoader().getResource("papers.txt").getFile())));

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
        log.info(ConfusionStatistics.calculateScore(tp, fp, tn, fn));
    }

    @Ignore
    @Test
    public void testSvmProcessorTestData() throws Exception {
        log.info("Testing with test data");
        // Get test data parsed...
        List<Paper> testPapers = PaperUtil.annotatePapers(LoadPapers
                .loadNewPapers(new File(getClass().getClassLoader().getResource("papers_test.txt").getFile())));
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;

        for (Paper paper : testPapers) {
            boolean previousWordKeyPhrase = false;
            for (CoreMap sentence : paper.getCoreNLPAnnotations()) {
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
        log.info(ConfusionStatistics.calculateScore(tp, fp, tn, fn));
    }

    @Test
    public void testSvmPredictKeyPhrases() {
        log.info("Testing with test data, getting key phrase objects");
        // Get test data parsed...
        List<Paper> testPapers = PaperUtil.annotatePapers(LoadPapers
                .loadNewPapers(new File(getClass().getClassLoader().getResource("papers_test.txt").getFile())));

        List<ConfusionStatistics> overallStatsGen = new ArrayList<ConfusionStatistics>();
        List<ConfusionStatistics> overallStatsInc = new ArrayList<ConfusionStatistics>();
        List<ConfusionStatistics> overallStatsStr = new ArrayList<ConfusionStatistics>();
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

        log.info("Overall statistics (gen): " + ConfusionStatistics.calculateScoreSum(overallStatsGen));
        log.info("Overall statistics (inc): " + ConfusionStatistics.calculateScoreSum(overallStatsInc));
        log.info("Overall statistics (str): " + ConfusionStatistics.calculateScoreSum(overallStatsStr));
    }

    @Ignore
    @Test
    public void testSvmPredictSaveToFile() throws IOException {
        String saveLocation = System.getenv("FYP_HOME") + "../testing/pred/";

        List<Paper> testPapers = PaperUtil.annotatePapers(LoadPapers
                .loadNewPapers(new File(getClass().getClassLoader().getResource("papers_test.txt").getFile())));

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
