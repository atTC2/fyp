package xyz.tomclarke.fyp.nlp.svm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;

import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import libsvm.svm_node;
import libsvm.svm_problem;
import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Uses libsvm to extract relations between key phrases using Word2Vec
 * 
 * @author tbc452
 *
 */
public class RelationshipSVM extends BaseSvm {

    private static final long serialVersionUID = -8495102790485498671L;

    private static final Logger log = LogManager.getLogger(RelationshipSVM.class);

    private static final int numOfSVs = 300;

    private double largest;
    private double smallest;
    private double scale;
    private RelationType type;

    public RelationshipSVM() {
        super();
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
     * @param vec
     *            The Word2Vec data to use
     * @param ann
     *            The annotator to use
     * @throws Exception
     *             If generating the support vectors fail
     */
    public void generateTrainingData(List<Paper> papers, RelationType type, Word2Vec vec, Annotator ann)
            throws Exception {
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
                        svm_node[] nodes = generateSupportVectors(ext1, ext2, vec, ann);
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
     * @param vec
     *            The Word2Vec data to use
     * @param ann
     *            The annotator to use
     * @return The support vector set representing the key phrase distances
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    public svm_node[] generateSupportVectors(KeyPhrase kp1, KeyPhrase kp2, Word2Vec vec, Annotator ann)
            throws Exception {
        // Get phrase vectors
        double[] vector1 = getPhraseVectorIgnoringStopWords(kp1, vec);
        double[] vector2 = getPhraseVectorIgnoringStopWords(kp2, vec);

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
     * Base results (14/12/2017): ``` Overall statistics: Accuracy: 0.99417070
     * Precision: 0.13402062 Recall: 0.06341463 F1: 0.08609272 Specific results
     * were: tp: 13.0 fp: 84.0 tn: 47058.0 fn: 192.0 ```
     * 
     * @param kp
     *            The key phrase
     * @param vec
     *            The Word2Vec data to use
     * @return The phrase vector
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    @SuppressWarnings("unused")
    private double[] getPhraseVector(KeyPhrase kp, Word2Vec vec) throws Exception {
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
     * Base results (14/12/2017): ``` Overall statistics: Accuracy: 0.99507961
     * Precision: 0.06666667 Recall: 0.00966184 F1: 0.01687764 Specific results
     * were: tp: 2.0 fp: 28.0 tn: 47119.0 fn: 205.0 ```
     * 
     * @param kp
     *            The key phrase
     * @param vec
     *            The Word2Vec data to use
     * @param ann
     *            The annotator to use
     * @return The phrase vector
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    @SuppressWarnings("unused")
    private double[] getPhraseVectorOfNoun(KeyPhrase kp, Word2Vec vec, Annotator ann) throws Exception {
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
     * Calculates a phrase vector from combining word vectors, ignoring stop words
     * if possible
     * 
     * @param kp
     *            The key phrase
     * @param vec
     *            The Word2Vec data to use
     * @return The phrase vector
     * @throws Exception
     *             If a word vector isn't the expected length.
     */
    private double[] getPhraseVectorIgnoringStopWords(KeyPhrase kp, Word2Vec vec) throws Exception {
        // Remove punctuation and get each word
        String[] tokens = NlpUtil.getAllTokens(kp.getPhrase());
        List<String> tokensToProcess = new ArrayList<String>();
        // Ensure there is at least 1 non-stop word
        for (String token : NlpUtil.getAllTokens(kp.getPhrase())) {
            if (!NlpUtil.isTokenToIgnore(token)) {
                tokensToProcess.add(token);
            }
        }

        // If there are tokens which aren't ignored, just process on these
        if (!tokensToProcess.isEmpty()) {
            tokens = tokensToProcess.toArray(new String[tokensToProcess.size()]);
        }

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
     * Generate SVs of the data
     * 
     * @param paper
     *            The paper to analyse
     * @param vec
     *            The Word2Vec data to use
     * @param ann
     *            The annotator to use
     * @return A list of relationships between key words
     * @throws Exception
     *             If generating support vectors went wrong
     */
    public List<svm_node[]> generateTestingVectors(Paper paper, Word2Vec vec, Annotator ann) throws Exception {
        List<svm_node[]> svs = new ArrayList<svm_node[]>();

        for (KeyPhrase ext1 : paper.getKeyPhrases()) {
            for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                if (!ext1.equals(ext2)) {
                    // Calculate a sv set
                    svs.add(generateSupportVectors(ext1, ext2, vec, ann));
                }
            }
        }

        return svs;
    }

    /**
     * Predict relationships in a paper
     * 
     * @param paper
     *            The paper to analyse
     * @param svs
     *            Pre-calculated support vectors for this test set
     * @return A list of relationships between key words
     * @throws Exception
     *             If generating support vectors went wrong
     */
    public List<Relationship> predictRelationshipsFromSvs(Paper paper, List<svm_node[]> svs) throws Exception {
        List<Relationship> rels = new ArrayList<Relationship>();

        for (int i = 0; i < paper.getKeyPhrases().size(); i++) {
            KeyPhrase ext1 = paper.getKeyPhrases().get(i);
            for (int j = 0; j < paper.getKeyPhrases().size(); j++) {
                KeyPhrase ext2 = paper.getKeyPhrases().get(j);
                if (!ext1.equals(ext2)) {
                    // Calculate a sv set, and see if it fits the type
                    svm_node[] nodes = svs.get(i + j);
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

    /**
     * Predict relationships in a paper
     * 
     * @param paper
     *            The paper to analyse
     * @param vec
     *            The Word2Vec data to use
     * @param ann
     *            The annotator to use
     * @return A list of relationships between key words
     * @throws Exception
     *             If generating support vectors went wrong
     */
    public List<Relationship> predictRelationships(Paper paper, Word2Vec vec, Annotator ann) throws Exception {
        List<Relationship> rels = new ArrayList<Relationship>();

        for (KeyPhrase ext1 : paper.getKeyPhrases()) {
            for (KeyPhrase ext2 : paper.getKeyPhrases()) {
                if (!ext1.equals(ext2)) {
                    // Calculate a sv set, and see if it fits the type
                    svm_node[] nodes = generateSupportVectors(ext1, ext2, vec, ann);
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
