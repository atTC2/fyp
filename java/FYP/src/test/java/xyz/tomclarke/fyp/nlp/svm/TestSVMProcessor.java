package xyz.tomclarke.fyp.nlp.svm;

import java.util.List;

import org.junit.Test;

import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistics;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;
import xyz.tomclarke.fyp.nlp.preprocessing.PreProcessor;

/**
 * Test to evaluate the SVM Processor
 * 
 * @author tbc452
 *
 */
public class TestSVMProcessor {

    @Test
    public void testSvmProcessor() throws Exception {
        List<Paper> papers = LoadPapers.loadNewPapers();
        PreProcessor pp = null;
        for (Paper paper : papers) {
            if (paper.getCoreNLPAnnotations() == null) {
                if (pp == null) {
                    pp = new PreProcessor();
                }
                pp.annotate(paper);
            }
        }

        SVMProcessor svm = new SVMProcessor();
        svm.generateTrainingData(papers);
        svm.train();

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

        System.out.println(ConfusionStatistics.calculateScore(tp, fp, tn, fn));
    }

}
