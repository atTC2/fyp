package xyz.tomclarke.fyp.nlp.svm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
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
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Test to evaluate the SVM Processor
 * 
 * @author tbc452
 *
 */
public class TestKeyPhraseSVM {

    private static final Logger log = LogManager.getLogger(TestKeyPhraseSVM.class);

    private static KeyPhraseSVM svmGeneral;
    private static List<Paper> trainingPapers;
    private static List<Paper> testPapers;
    private static Word2Vec vec;

    @BeforeClass
    public static void initalise() {
        log.info("Loading training and test data...");
        trainingPapers = NlpUtil.loadAndAnnotatePapers(TestKeyPhraseSVM.class);
        testPapers = NlpUtil.loadAndAnnotateTestPapers(TestKeyPhraseSVM.class);

        // Load Word2Vec
        log.info("Loading Word2Vec");
        // TODO, try different ones
        vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
    }

    /**
     * Builds the general purpose key phrase extractor SVM
     * 
     * @throws Exception
     */
    private static void loadGeneralSvm() throws Exception {
        if (svmGeneral == null) {
            // Setup the SVM
            log.info("Building general key phrase SVM...");
            svmGeneral = new KeyPhraseSVM(vec);
            svmGeneral.generateTrainingData(trainingPapers, null);
            svmGeneral.train();

            log.info("SVM trained, now to test...");
        }
    }

    @Ignore
    @Test
    public void testSvmProcessorSameData() throws Exception {
        loadGeneralSvm();
        log.info("Testing with training data");
        // Test on input data...
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;
        svm_problem problem = svmGeneral.getProblem();
        for (int i = 0; i < problem.l; i++) {
            boolean isPredictedKeyPhrase = svmGeneral.predict(problem.x[i]);
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
        loadGeneralSvm();
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
                    svm_node[] nodes = svmGeneral.generateSupportVectors(token, paper, previousWordKeyPhrase);

                    // Ask the question and compare the answer to the expected answer
                    boolean isPredictedKeyPhrase = svmGeneral.predict(nodes);
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
    public void testSvmPredictKeyPhrases() throws Exception {
        loadGeneralSvm();
        log.info("Testing with test data, getting key phrase key phrase extraction objects");
        List<ConfusionStatistic> overallStatsGen = new ArrayList<ConfusionStatistic>();
        List<ConfusionStatistic> overallStatsInc = new ArrayList<ConfusionStatistic>();
        List<ConfusionStatistic> overallStatsStr = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            List<KeyPhrase> phrases = svmGeneral.predictKeyPhrases(paper);

            // Log them
            printKP(paper, phrases);

            // Save statistics
            overallStatsGen.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getKeyPhrases(),
                    Strictness.GENEROUS, false));
            overallStatsInc.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getKeyPhrases(),
                    Strictness.INCLUSIVE, false));
            overallStatsStr.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getKeyPhrases(),
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
    public void testSvmKeyPhraseClassifications() throws Exception {
        log.info("Building task key phrase SVM...");
        KeyPhraseSVM svmTask = new KeyPhraseSVM(vec);
        svmTask.generateTrainingData(trainingPapers, Classification.TASK);
        svmTask.train();
        log.info("Building process key phrase SVM...");
        KeyPhraseSVM svmProcess = new KeyPhraseSVM(vec);
        svmProcess.generateTrainingData(trainingPapers, Classification.PROCESS);
        svmProcess.train();
        log.info("Building material key phrase SVM...");
        KeyPhraseSVM svmMaterial = new KeyPhraseSVM(vec);
        svmMaterial.generateTrainingData(trainingPapers, Classification.MATERIAL);
        svmMaterial.train();
        log.info("SVMs trained, now to test key phrase extraction and classification...");

        List<ConfusionStatistic> overallStatsGen = new ArrayList<ConfusionStatistic>();
        List<ConfusionStatistic> overallStatsInc = new ArrayList<ConfusionStatistic>();
        List<ConfusionStatistic> overallStatsStr = new ArrayList<ConfusionStatistic>();
        for (Paper paper : testPapers) {
            List<KeyPhrase> tasks = svmTask.predictKeyPhrases(paper);
            List<KeyPhrase> processes = svmProcess.predictKeyPhrases(paper);
            List<KeyPhrase> materials = svmMaterial.predictKeyPhrases(paper);
            List<KeyPhrase> phrases = new ArrayList<KeyPhrase>();

            // Log them
            printKP(paper, phrases);

            phrases.addAll(tasks);
            phrases.addAll(processes);
            phrases.addAll(materials);

            // Save statistics
            overallStatsGen.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getKeyPhrases(),
                    Strictness.GENEROUS, true));
            overallStatsInc.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getKeyPhrases(),
                    Strictness.INCLUSIVE, true));
            overallStatsStr.add(EvaluateExtractions.evaluateKeyPhrases(phrases, paper, paper.getKeyPhrases(),
                    Strictness.STRICT, true));
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

    /**
     * Debug log extractions
     * 
     * @param paper
     *            The associated paper
     * @param phrases
     *            The phrases found
     */
    private void printKP(Paper paper, List<KeyPhrase> phrases) {
        log.debug("EXTRACTIONS FOUND");
        log.debug(paper.toString());
        for (KeyPhrase phrase : phrases) {
            log.debug(phrase.toString());
        }

    }

}
