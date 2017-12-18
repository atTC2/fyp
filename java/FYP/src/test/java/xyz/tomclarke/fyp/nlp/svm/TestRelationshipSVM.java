package xyz.tomclarke.fyp.nlp.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import libsvm.svm_node;
import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistic;
import xyz.tomclarke.fyp.nlp.evaluation.EvaluateExtractions;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Tests the relation extraction SVM which uses Word2Vec
 * 
 * @author tbc452
 *
 */
public class TestRelationshipSVM {

    private static final Logger log = LogManager.getLogger(TestRelationshipSVM.class);

    private static List<Paper> trainingPapers;
    private static List<Paper> testPapers;

    @BeforeClass
    public static void initalise() {
        log.info("Loading training and test data...");
        trainingPapers = NlpUtil.loadAndAnnotatePapers(TestRelationshipSVM.class);
        testPapers = NlpUtil.loadAndAnnotateTestPapers(TestRelationshipSVM.class);
    }

    @Test
    public void testRelationSvmAllW2V() throws FileNotFoundException, IOException {
        boolean successfull = true;
        File fout = new File("/home/tom/FYP/test.out");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout)))) {
            try {
                writeBw(bw, "Using Google News");
                testRelationSvm(Word2VecPretrained.GOOGLE_NEWS, bw);
            } catch (Exception e) {
                log.error("Error running GOOGLE_NEWS test", e);
                writeBw(bw, "Failed: " + e.getMessage());
                successfull = false;
            }
            System.gc();
            try {
                writeBw(bw, "Using Wiki2Vec");
                testRelationSvm(Word2VecPretrained.WIKI2VEC, bw);
            } catch (Exception e) {
                log.error("Error running WIKI2VEC test", e);
                writeBw(bw, "Failed: " + e.getMessage());
                successfull = false;
            }
            System.gc();
            try {
                writeBw(bw, "Using Freebase IDs");
                testRelationSvm(Word2VecPretrained.FREEBASE_IDS, bw);
            } catch (Exception e) {
                log.error("Error running FREEBASE_IDS test", e);
                writeBw(bw, "Failed: " + e.getMessage());
                successfull = false;
            }
            System.gc();
            try {
                writeBw(bw, "Using Freebase names");
                testRelationSvm(Word2VecPretrained.FREEBASE_NAMES, bw);
            } catch (Exception e) {
                log.error("Error running FREEBASE_NAMES test", e);
                writeBw(bw, "Failed: " + e.getMessage());
                successfull = false;
            }
        }

        Assert.assertTrue(successfull);
    }

    /**
     * Test an SVM with given config
     * 
     * @param set
     *            The W2V set to load
     * @param bw
     *            The writer to more efficient logging
     * @throws Exception
     */
    private void testRelationSvm(Word2VecPretrained set, BufferedWriter bw) throws Exception {
        // Get all relationships and evaluate
        List<List<Relationship>> allRels = new ArrayList<List<Relationship>>();
        writeBw(bw, "Doing hyp");
        log.info("RUNNING HYP SVM...");
        // Do hyp
        allRels.addAll(trainAndUseSvm(RelationType.HYPONYM_OF, set));
        System.gc();
        writeBw(bw, "Doing syn");
        log.info("RUNNING SYN SVM...");
        // Do syn
        allRels.addAll(trainAndUseSvm(RelationType.SYNONYM_OF, set));

        writeBw(bw, "Doing calculations");
        log.info("CALCULATING...");
        // Evaluate
        List<ConfusionStatistic> overallStats = new ArrayList<ConfusionStatistic>();
        for (int i = 0; i < testPapers.size(); i++) {
            overallStats.add(EvaluateExtractions.evaluateRelationships(allRels.get(i),
                    testPapers.get(i).getRelationships(), testPapers.get(i).getKeyPhrases()));
        }

        ConfusionStatistic stats = ConfusionStatistic.calculateScoreSum(overallStats);

        log.info("Overall statistics: " + stats);
        log.info("Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());

        writeBw(bw, "Overall statistics: " + stats);
        writeBw(bw, "Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());
    }

    /**
     * Loads resources and runs an SVM
     * 
     * @param type
     *            The type of SVM
     * @param set
     *            The Word2Vec data set to use
     * @return Calculated relationships (for all test papers, in same order as test
     *         papers)
     * @throws Exception
     */
    private List<List<Relationship>> trainAndUseSvm(RelationType type, Word2VecPretrained set) throws Exception {
        List<List<Relationship>> allRels = new ArrayList<List<Relationship>>();
        RelationshipSVM svm = new RelationshipSVM();

        // Generate the SVM training data
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(set);
        Annotator ann = new Annotator();
        svm.generateTrainingData(trainingPapers, type, vec, ann);

        // Get the testing vectors while vec and ann still loaded
        List<List<svm_node[]>> testSvs = new ArrayList<List<svm_node[]>>();
        for (Paper paper : testPapers) {
            testSvs.add(svm.generateTestingVectors(paper, vec, ann));
        }

        // Clear memory
        vec = null;
        ann = null;
        System.gc();

        // Train the SVM
        svm.train();

        // Use the pre-calculated test SVs to get relations
        for (int i = 0; i < testPapers.size(); i++) {
            allRels.add(svm.predictRelationshipsFromSvs(testPapers.get(i), testSvs.get(i)));
        }

        return allRels;
    }

    /**
     * Writes a line to a buffered writer
     * 
     * @param bw
     *            The buffer to write to
     * @param line
     *            The line to write
     * @throws IOException
     */
    private void writeBw(BufferedWriter bw, String line) throws IOException {
        bw.write(line);
        bw.newLine();
        bw.flush();
    }

}
