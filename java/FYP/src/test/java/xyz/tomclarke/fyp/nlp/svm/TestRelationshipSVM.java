package xyz.tomclarke.fyp.nlp.svm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.evaluation.ConfusionStatistic;
import xyz.tomclarke.fyp.nlp.evaluation.EvaluateExtractions;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Tests the relation extraction SVM which uses Word2Vec
 * 
 * @author tbc452
 *
 */
public class TestRelationshipSVM {

    private static final Logger log = LogManager.getLogger(TestRelationshipSVM.class);

    private static RelationshipSVM hypSvm;
    private static RelationshipSVM synSvm;
    private static List<Paper> trainingPapers;
    private static List<Paper> testPapers;
    private static Word2Vec vec;
    private static Annotator ann;

    @BeforeClass
    public static void initalise() {
        log.info("Loading training and test data...");
        trainingPapers = NlpUtil.loadAndAnnotatePapers(TestRelationshipSVM.class);
        testPapers = NlpUtil.loadAndAnnotateTestPapers(TestRelationshipSVM.class);

        // Load Word2Vec
        log.info("Loading Word2Vec and Annotator");
        vec = Word2VecProcessor.loadGoogleNewsVectors();
        ann = new Annotator();
        log.info("Test initialised");
    }

    /**
     * Builds the hyponym SVM
     * 
     * @throws Exception
     */
    public static void buildHypSvm() throws Exception {
        if (hypSvm == null) {
            // Setup the SVM
            log.info("Building hyponym SVM...");
            hypSvm = new RelationshipSVM(vec, ann);
            hypSvm.generateTrainingData(trainingPapers, RelationType.HYPONYM_OF);
            hypSvm.train();
        }
    }

    /**
     * Builds the synonym SVM
     * 
     * @throws Exception
     */
    public static void buildSynSvm() throws Exception {
        if (synSvm == null) {
            // Setup the SVM
            log.info("Building synonym SVM...");
            synSvm = new RelationshipSVM(vec, ann);
            synSvm.generateTrainingData(trainingPapers, RelationType.SYNONYM_OF);
            synSvm.train();
        }
    }

    @Ignore
    @Test
    public void testHypSvm() throws Exception {
        buildHypSvm();
        for (Paper paper : testPapers) {
            List<Relationship> rels = hypSvm.predictRelationships(paper);
            log.info("Hyponyms for " + paper.toString());
            for (Relationship rel : rels) {
                log.info(rel);
            }
        }
    }

    @Ignore
    @Test
    public void testSynSvm() throws Exception {
        buildSynSvm();
        for (Paper paper : testPapers) {
            List<Relationship> rels = synSvm.predictRelationships(paper);
            log.info("Synonyms for " + paper.toString());
            for (Relationship rel : rels) {
                log.info(rel);
            }
        }
    }

    @Test
    public void testRelationSvm() throws Exception {
        // Get all relationships and evaluate
        List<ConfusionStatistic> overallStats = new ArrayList<ConfusionStatistic>();
        buildHypSvm();
        buildSynSvm();
        for (Paper paper : testPapers) {
            List<Relationship> rels = hypSvm.predictRelationships(paper);
            rels.addAll(synSvm.predictRelationships(paper));
            overallStats.add(
                    EvaluateExtractions.evaluateRelationships(rels, paper.getRelationships(), paper.getKeyPhrases()));
        }

        ConfusionStatistic stats = ConfusionStatistic.calculateScoreSum(overallStats);

        log.info("Overall statistics: " + stats);
        log.info("Specific results were: tp: " + stats.getTp() + " fp: " + stats.getFp() + " tn: " + stats.getTn()
                + " fn: " + stats.getFn());
    }

}
