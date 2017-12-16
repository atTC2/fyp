package xyz.tomclarke.fyp.nlp.svm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;

import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;

/**
 * Uses libsvm to extract relations between key phrases using Word2Vec
 * 
 * @author tbc452
 *
 */
public class RelationshipSVM extends BaseSvm {

    private static final Logger log = LogManager.getLogger(RelationshipSVM.class);

    private static final int numOfSVs = 300;

    private Word2Vec vec;
    private Annotator ann;
    private double largest;
    private double smallest;
    private double scale;
    private RelationType type;

    public RelationshipSVM(Word2Vec vec, Annotator ann) {
        super();
        // Construct the (default) parameter object
        param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        // 1 / number of features
        param.gamma = 1 / 2.0;
        param.cache_size = 1024;
        param.eps = 0.001;
        param.C = 100.0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        param.shrinking = 0;
        param.probability = 0;

        this.vec = vec;
        this.ann = ann;

        // Used for scaling
        smallest = 0.0;
        largest = 0.0;
        scale = 0.0;
    }

    /**
     * Generate training support vectors
     * 
     * @param papers
     *            The training papers
     * @param type
     *            The relation type
     * @throws Exception
     *             If generating the support vectors fail
     */
    public void generateTrainingData(List<Paper> papers, RelationType type) throws Exception {
        log.info("Generating data for SVM for " + type.toString() + " extraction");
        this.type = type;
        // The problem to return
        problem = new svm_problem();
        // Will have 1 entry for each pair of key phrases in a paper
        int numOfCombinations = 0;
        for (Paper paper : papers) {
            for (KeyPhrase ext1 : paper.getKeyPhrases()) {
                for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                    if (!ext1.equals(ext2)) {
                        numOfCombinations++;
                        // This only counts non equal combinations
                        // Repeat combinations are allowed, as it matters which direction the link is
                        // from for hypernyms
                    }
                }
            }
        }

        problem.l = numOfCombinations;
        problem.y = new double[problem.l];
        problem.x = new svm_node[problem.l][numOfSVs];

        // Build each combination
        int index = 0;
        for (Paper paper : papers) {
            for (KeyPhrase ext1 : paper.getKeyPhrases()) {
                for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                    if (!ext1.equals(ext2)) {
                        // Calculate a sv set, and see if it fits the type
                        svm_node[] nodes = generateSupportVectors(ext1, ext2);
                        double isRel = 0.0;
                        for (Relationship rel : paper.getRelationships()) {
                            // TODO add support for >2 KP for synonyms.
                            if (rel.getType().equals(type) && rel.getPhrases()[0].equals(ext1)
                                    && rel.getPhrases()[1].equals(ext2)) {
                                isRel = 1.0;
                                break;
                            }
                        }

                        // Add the information
                        problem.y[index] = isRel;
                        problem.x[index] = nodes;

                        index++;
                    }
                }
            }
        }

        if (numOfCombinations != index) {
            log.warn("Problem length not equal to index achieved: " + numOfCombinations + " -> " + index);
        }

        // Scale the data
        if (largest > Math.abs(smallest)) {
            scale = largest;
        } else {
            scale = Math.abs(smallest);
        }
        log.info("Scale: " + String.valueOf(scale));
        for (svm_node[] nodes : problem.x) {
            for (svm_node node : nodes) {
                node.value /= scale;
            }
        }
    }

    /**
     * Generate the vector distance between two key phrases
     * 
     * @param kp1
     *            The key phrase from
     * @param kp2
     *            The key phrase to
     * @return The support vector set representing the key phrase distances
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    public svm_node[] generateSupportVectors(KeyPhrase kp1, KeyPhrase kp2) throws Exception {
        // Get phrase vectors
        double[] vector1 = getPhraseVectorOfNoun(kp1);
        double[] vector2 = getPhraseVectorOfNoun(kp2);

        // Build the SVs for the given token
        svm_node[] nodes = new svm_node[numOfSVs];
        for (int i = 0; i < numOfSVs; i++) {
            double diff = vector1[i] - vector2[i];

            if (scale > 0.0) {
                // Already set a scale, use it
                nodes[i] = makeNewNode(i, diff / scale);
            } else {
                // Still need to set a scale, so raw values for now and help figure out the
                // scale
                nodes[i] = makeNewNode(i, diff);
                if (diff > largest) {
                    largest = diff;
                }
                if (diff < smallest) {
                    smallest = diff;
                }
            }
        }
        return nodes;
    }

    /**
     * Calculates a phrase vector from combining word vectors
     * 
     * @param kp
     *            The key phrase
     * @return The phrase vector
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    @SuppressWarnings("unused")
    private double[] getPhraseVector(KeyPhrase kp) throws Exception {
        // Remove punctuation and get each word
        String[] tokens = kp.getPhrase().replaceAll("\\p{Punct}", "").split(" ");

        int expectedVectorLength = numOfSVs;
        double[] vector = new double[expectedVectorLength];

        // Sum token vectors, ignoring words that are not in Word2Vec
        for (String token : tokens) {
            if (vec.hasWord(token)) {
                double[] wordVector = vec.getWordVector(token);
                if (expectedVectorLength != wordVector.length) {
                    throw new Exception("Word Vector length not " + expectedVectorLength);
                }
                for (int i = 0; i < wordVector.length; i++) {
                    vector[i] += wordVector[i];
                }
            }
        }

        return vector;
    }

    /**
     * Calculates a phrase vector from searching the base noun in Word2Vec
     * 
     * @param kp
     *            The key phrase
     * @return The phrase vector
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    private double[] getPhraseVectorOfNoun(KeyPhrase kp) throws Exception {
        // Remove punctuation and get each word
        List<CoreMap> sentences = ann.annotate(kp.getPhrase());
        if (sentences.size() > 1) {
            log.warn("Key Phrase '" + kp.getPhrase() + "' annotated to " + sentences.size() + " sentences!");
        }
        CoreMap sentence = sentences.get(0);
        String deepestNoun = ann.getDeepestNoun(sentence.get(TreeCoreAnnotations.TreeAnnotation.class), 0).getKey();

        if (deepestNoun == null) {
            // No deepest noun.. so try the whole thing?
            deepestNoun = kp.getPhrase();
        }
        deepestNoun = deepestNoun.replaceAll("\\p{Punct}", "").replaceAll(" ", "_");
        log.debug("NOUN SELECTED: " + kp.getPhrase() + " -> " + deepestNoun + " in vec: " + vec.hasWord(deepestNoun));

        int expectedVectorLength = numOfSVs;
        double[] vector = new double[expectedVectorLength];

        if (vec.hasWord(deepestNoun)) {
            vector = vec.getWordVector(deepestNoun);
        } else {
            // Sum token vectors, ignoring words that are not in Word2Vec
            for (String token : deepestNoun.split("_")) {
                if (vec.hasWord(token)) {
                    double[] wordVector = vec.getWordVector(token);
                    if (expectedVectorLength != wordVector.length) {
                        throw new Exception("Word Vector length not " + expectedVectorLength);
                    }
                    for (int i = 0; i < wordVector.length; i++) {
                        vector[i] += wordVector[i];
                    }
                }
            }
        }

        return vector;
    }

    /**
     * Predict relationships in a paper
     * 
     * @param paper
     *            The paper to analyse
     * @return A list of relationships between key words
     * @throws Exception
     *             If generating support vectors went wrong
     */
    public List<Relationship> predictRelationships(Paper paper) throws Exception {
        List<Relationship> rels = new ArrayList<Relationship>();

        for (KeyPhrase ext1 : paper.getKeyPhrases()) {
            for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                if (!ext1.equals(ext2)) {
                    // Calculate a sv set, and see if it fits the type
                    svm_node[] nodes = generateSupportVectors(ext1, ext2);
                    if (predict(nodes)) {
                        // TODO sort out relationship IDs
                        Relationship newRel = new Relationship(0, type, new KeyPhrase[] { ext1, ext2 });
                        rels.add(newRel);
                    }
                }
            }
        }

        return rels;
    }
}
