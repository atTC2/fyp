package xyz.tomclarke.fyp.nlp.svm;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistics;
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

	private static SVMProcessor svm;

	@BeforeClass
	public static void initalise() throws Exception {
		List<Paper> papers = PaperUtil.annotatePapers(LoadPapers.loadNewPapers(
				new File(new LoadPapers().getClass().getClassLoader().getResource("papers.txt").getFile())));

		// Setup the SVM
		svm = new SVMProcessor();
		svm.generateTrainingData(papers);
		svm.train();
	}

	@Test
	public void testSvmProcessorSameData() throws Exception {
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

		ConfusionStatistics stats = ConfusionStatistics.calculateScore(tp, fp, tn, fn);
		System.out.println(stats);
		Assert.assertTrue(stats.getAccuracy() == 1.0);
		Assert.assertTrue(stats.getPrecision() == 1.0);
		Assert.assertTrue(stats.getRecall() == 1.0);
		Assert.assertTrue(stats.getFOne() == 1.0);
	}

	@Test
	public void testSvmProcessorTestData() throws Exception {
		// Get test data parsed...
		List<Paper> testPapers = PaperUtil.annotatePapers(LoadPapers.loadNewPapers(
				new File(new LoadPapers().getClass().getClassLoader().getResource("papers_test.txt").getFile())));
		SVMProcessor svmTest = new SVMProcessor();
		svmTest.generateTrainingData(testPapers);

		// Test on input data...
		double tp = 0;
		double fp = 0;
		double tn = 0;
		double fn = 0;
		svm_problem problem = svmTest.getProblem();
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

		System.out.println(String.format("tp: %.8f fp: %.8f tn: %.8f fn: %.8f", tp, fp, tn, fn));
		System.out.println(ConfusionStatistics.calculateScore(tp, fp, tn, fn));
	}

}
