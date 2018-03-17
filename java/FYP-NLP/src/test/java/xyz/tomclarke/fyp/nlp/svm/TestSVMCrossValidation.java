package xyz.tomclarke.fyp.nlp.svm;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.TestOnPapers;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.util.NlpError;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Runs cross validation on SVMs
 * 
 * @author tbc452
 *
 */
public class TestSVMCrossValidation extends TestOnPapers {

    private static final Logger log = LogManager.getLogger(TestSVMCrossValidation.class);
    private static Word2Vec vec;

    @BeforeClass
    public static void init() {
        vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
    }

    /**
     * KeyPhraseSVM tests
     */

    /**
     * C = 5
     */

    @Test
    public void testKpCv5And1() throws IOException {
        testKeyPhraseCrossValidation(5.0, 1.0);
    }

    @Test
    public void testKpCv5AndHalf() throws IOException {
        testKeyPhraseCrossValidation(5.0, 0.5);
    }

    @Test
    public void testKpCv5AndQuarter() throws IOException {
        testKeyPhraseCrossValidation(5.0, 0.25);
    }

    /**
     * C = 50
     */

    @Test
    public void testKpCv50And1() throws IOException {
        testKeyPhraseCrossValidation(50.0, 1.0);
    }

    @Test
    public void testKpCv50AndHalf() throws IOException {
        testKeyPhraseCrossValidation(50.0, 0.5);
    }

    @Test
    public void testKpCv50AndQuarter() throws IOException {
        testKeyPhraseCrossValidation(50.0, 0.25);
    }

    /**
     * C = 100
     */

    @Test
    public void testKpCv100And1() throws IOException {
        testKeyPhraseCrossValidation(100.0, 1.0);
    }

    @Test
    public void testKpCv100AndHalf() throws IOException {
        testKeyPhraseCrossValidation(100.0, 0.5);
    }

    @Test
    public void testKpCv100AndQuarter() throws IOException {
        testKeyPhraseCrossValidation(100.0, 0.25);
    }

    /**
     * C = 200
     */

    @Test
    public void testKpCv200And1() throws IOException {
        testKeyPhraseCrossValidation(200.0, 1.0);
    }

    @Test
    public void testKpCv200AndHalf() throws IOException {
        testKeyPhraseCrossValidation(200.0, 0.5);
    }

    @Test
    public void testKpCv200AndQuarter() throws IOException {
        testKeyPhraseCrossValidation(200.0, 0.25);
    }

    /**
     * C = 500
     */

    @Ignore
    @Test
    public void testKpCv500And1() throws IOException {
        testKeyPhraseCrossValidation(500.0, 1.0);
    }

    @Ignore
    @Test
    public void testKpCv500AndHalf() throws IOException {
        testKeyPhraseCrossValidation(500.0, 0.5);
    }

    @Ignore
    @Test
    public void testKpCv500AndQuarter() throws IOException {
        testKeyPhraseCrossValidation(500.0, 0.25);
    }

    /**
     * Runs cross validation on the key phrase SVM with given C and gamma values
     * 
     * @param c
     *            The C value to use
     * @param gamma
     *            The gamma value to use
     * @throws IOException
     *             If something goes wrong
     */
    private void testKeyPhraseCrossValidation(double c, double gamma) throws IOException {
        KeyPhraseSVM svm = new KeyPhraseSVM();
        svm.generateTrainingData(trainingPapers, null, vec);
        double accuracy = svm.doCrossValidation(c, gamma);
        log.info("Cross Validation Accuracy = " + accuracy + "%");
    }

    /**
     * RelationshipSVM2 tests
     */

    /**
     * C = 5
     */

    @Test
    public void testRelCv5And1() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 5.0, 1.0);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 5.0, 1.0);
    }

    @Test
    public void testRelCv5AndHalf() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 5.0, 0.5);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 5.0, 0.5);
    }

    @Test
    public void testRelCv5AndQuarter() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 5.0, 0.25);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 5.0, 0.25);
    }

    /**
     * C = 50
     */

    @Test
    public void testRelCv50And1() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 50.0, 1.0);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 50.0, 1.0);
    }

    @Test
    public void testRelCv50AndHalf() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 50.0, 0.5);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 50.0, 0.5);
    }

    @Test
    public void testRelCv50AndQuarter() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 50.0, 0.25);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 50.0, 0.25);
    }

    /**
     * C = 100
     */

    @Test
    public void testRelCv100And1() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 100.0, 1.0);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 100.0, 1.0);
    }

    @Test
    public void testRelCv100AndHalf() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 100.0, 0.5);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 100.0, 0.5);
    }

    @Test
    public void testRelCv100AndQuarter() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 100.0, 0.25);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 100.0, 0.25);
    }

    /**
     * C = 200
     */

    @Test
    public void testRelCv200And1() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 200.0, 1.0);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 200.0, 1.0);
    }

    @Test
    public void testRelCv200AndHalf() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 200.0, 0.5);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 200.0, 0.5);
    }

    @Test
    public void testRelCv200AndQuarter() throws IOException, NlpError {
        testRelationshipCrossValidation(RelationType.HYPONYM_OF, 200.0, 0.25);
        testRelationshipCrossValidation(RelationType.SYNONYM_OF, 200.0, 0.25);
    }

    /**
     * Runs cross validation on the relation SVM (2) with given C and gamma values
     * 
     * @param type
     *            The type of relationship to test
     * @param c
     *            The C value to use
     * @param gamma
     *            The gamma value to use
     * @throws IOException
     *             If something goes wrong
     * @throws NlpError
     */
    private void testRelationshipCrossValidation(RelationType type, double c, double gamma)
            throws IOException, NlpError {
        RelationshipSVM2 svm = new RelationshipSVM2();
        svm.generateTrainingData(trainingPapers, type, vec);
        double accuracy = svm.doCrossValidation(c, gamma);
        log.info("Cross Validation Accuracy = " + accuracy + "% on" + type);
    }

}
