package xyz.tomclarke.fyp.nlp.svm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;

import libsvm.svm_node;
import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;
import xyz.tomclarke.fyp.nlp.util.NlpError;

/**
 * Uses libsvm to extract relations between key phrases using Word2Vec
 * 
 * @author tbc452
 *
 */
public class RelationshipSVM2 extends BaseSvm {

    private static final long serialVersionUID = -2532987739845268447L;

    private static final Logger log = LogManager.getLogger(RelationshipSVM2.class);

    private static final int NUM_OF_SVS = 2;
    private static final int W2V_EXPECTED_LEN = 300;

    private RelationType type;

    public RelationshipSVM2() {
        super();
    }

    /**
     * Generate training support vectors
     * 
     * @param papers
     *            The training papers
     * @param type
     *            The relation type
     * @param vec
     *            The Word2Vec data to use
     * @throws NlpError
     *             If generating the support vectors fail
     */
    public void generateTrainingData(List<Paper> papers, RelationType type, Word2Vec vec) throws NlpError {
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
        problem.x = new svm_node[problem.l][NUM_OF_SVS];

        // Build each combination
        int index = 0;
        for (Paper paper : papers) {
            for (KeyPhrase ext1 : paper.getKeyPhrases()) {
                for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                    if (!ext1.equals(ext2)) {
                        // Calculate a sv set, and see if it fits the type
                        svm_node[] nodes = generateSupportVectors(ext1, ext2, vec);
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
    }

    /**
     * Generate the vector distance between two key phrases
     * 
     * @param kp1
     *            The key phrase from
     * @param kp2
     *            The key phrase to
     * @param vec
     *            The Word2Vec data to use
     * @return The support vector set representing the key phrase distances
     * @throws NlpError
     *             If a word vector isn't the expected length.
     */
    public svm_node[] generateSupportVectors(KeyPhrase kp1, KeyPhrase kp2, Word2Vec vec) throws NlpError {
        // Get phrase vectors
        double[] v1 = getPhraseVector(kp1, vec);
        double[] v2 = getPhraseVector(kp2, vec);

        // Build the SVs for the given token
        svm_node[] nodes = new svm_node[NUM_OF_SVS];

        // Calculate distance and angle

        // Distance
        double diffSquareSum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            diffSquareSum += (v1[i] - v2[i]) * (v1[i] - v2[i]);
        }
        svm_node svDistance = makeNewNode(0, Math.sqrt(diffSquareSum));

        // Angle
        double v1Len = 0.0;
        double v2Len = 0.0;
        double dotProduct = 0.0;
        for (int i = 0; i < v1.length; i++) {
            v1Len += v1[i] * v1[i];
            v2Len += v2[i] * v2[i];

            dotProduct += v1[i] * v2[i];
        }
        v1Len = Math.sqrt(v1Len);
        v2Len = Math.sqrt(v2Len);
        double cosAngle = dotProduct / (v1Len * v2Len);
        svm_node svAngle = makeNewNode(1, Math.acos(cosAngle));

        nodes[0] = svDistance;
        nodes[1] = svAngle;
        return nodes;
    }

    /**
     * Calculates a phrase vector from combining word vectors
     *
     * 
     *
     * @param kp
     *            The key phrase
     * @param vec
     *            The Word2Vec data to use
     * @return The phrase vector
     * @throws NlpError
     *             If a word vector isn't the expected length.
     */
    private double[] getPhraseVector(KeyPhrase kp, Word2Vec vec) throws NlpError {
        // Remove punctuation and get each word
        String[] tokens = kp.getPhrase().replaceAll("\\p{Punct}", "").split(" ");

        double[] vector = new double[W2V_EXPECTED_LEN];

        // Sum token vectors, ignoring words that are not in Word2Vec
        for (String token : tokens) {
            if (vec.hasWord(token)) {
                double[] wordVector = vec.getWordVector(token);
                if (W2V_EXPECTED_LEN != wordVector.length) {
                    throw new NlpError("Word Vector length not " + W2V_EXPECTED_LEN);
                }
                for (int i = 0; i < wordVector.length; i++) {
                    vector[i] += wordVector[i];
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
     * @param vec
     *            The Word2Vec data to use
     * @return A list of relationships between key words
     * @throws Exception
     *             If generating support vectors went wrong
     */
    public List<Relationship> predictRelationships(Paper paper, Word2Vec vec) throws Exception {
        List<Relationship> rels = new ArrayList<Relationship>();

        for (KeyPhrase ext1 : paper.getKeyPhrases()) {
            for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                if (!ext1.equals(ext2)) {
                    // Calculate a sv set, and see if it fits the type
                    svm_node[] nodes = generateSupportVectors(ext1, ext2, vec);
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
