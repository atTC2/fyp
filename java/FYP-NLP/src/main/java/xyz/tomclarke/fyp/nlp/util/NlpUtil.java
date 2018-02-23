package xyz.tomclarke.fyp.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;
import xyz.tomclarke.fyp.nlp.preprocessing.PreProcessor;

/**
 * A collection of useful utilities
 * 
 * @author tbc452
 *
 */
public final class NlpUtil {

    private static final Logger log = LogManager.getLogger(NlpUtil.class);
    private static List<String> ignoreList;

    public static final double TF_IDF_THRESHOLD_PHRASE = 0.02;
    public static final double TF_IDF_THRESHOLD_TOKEN = 0.007;
    public static final String REGEX_ALL_PUNCTUATION = "[^a-zA-Z0-9 ]";

    private NlpUtil() {
        // Nothing to do here
    }

    /**
     * Decides if it is a token to ignore
     * 
     * It is the stop words from Standford NLP
     * https://github.com/stanfordnlp/CoreNLP/blob/master/data/edu/stanford/nlp/patterns/surface/stopwords.txt
     * 
     * @param token
     *            The token to check
     * @return If the token should be ignored
     */
    public static boolean isTokenToIgnore(String token) {
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
            try (BufferedReader br = new BufferedReader(
                    new FileReader(NlpUtil.class.getClassLoader().getResource("ignore.txt").getFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    ignoreList.add(line.toLowerCase());
                }
            } catch (IOException e) {
                log.error("Could not read token ignore file", e);
            }
        }

        return ignoreList.contains(token.toLowerCase());
    }

    /**
     * Loads papers and annotates them
     * 
     * @param clazz
     *            The class to load them from (if it's a test/ class, it'll load
     *            from test/resources).
     * @param canAttemtAnnRead
     *            Specifies whether the paper object should be saved to disk
     * @return Annotated papers
     */
    public static <T> List<Paper> loadAndAnnotatePapers(Class<T> clazz, boolean canAttemptAnnRead) {
        return annotatePapers(LoadPapers.loadNewPapers(
                new File(clazz.getClassLoader().getResource("papers.txt").getFile()), canAttemptAnnRead, true));
    }

    /**
     * Loads test papers and annotates them
     * 
     * @param clazz
     *            The class to load them from (if it's a test/ class, it'll load
     *            from test/resources).
     * @return Annotated test papers
     */
    public static <T> List<Paper> loadAndAnnotateTestPapers(Class<T> clazz) {
        return annotatePapers(LoadPapers
                .loadNewPapers(new File(clazz.getClassLoader().getResource("papers_test.txt").getFile()), true, true));
    }

    /**
     * Runs the Stanford CoreNLP annotator on all papers supplied
     * 
     * @param papers
     *            papers to annotate
     * @return The annotated papers
     */
    public static List<Paper> annotatePapers(List<Paper> papers) {
        PreProcessor pp = null;
        for (Paper paper : papers) {
            if (paper.getAnnotations() == null) {
                if (pp == null) {
                    pp = new PreProcessor();
                }
                pp.annotate(paper);
            }
        }

        return papers;
    }

    /**
     * Converts words to tokens (lower case, removes all punctuation, splits on
     * spaces)
     * 
     * @param words
     *            The words to get tokens from
     * @return A list of tokens
     */
    public static String[] getAllTokens(String words) {
        return sanitiseString(words).split(" ");
    }

    /**
     * Removes all punctuation from a word for processing
     * 
     * @param word
     *            The word to remove punctuation from
     * @return The sanitised string
     */
    private static String sanitiseString(String word) {
        return word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Calculates the TF-IDF of a word in a paper
     * 
     * @param originalWord
     *            The word to calculate the TF-IDF of
     * @param containingPaper
     *            The paper the word is from
     * @param trainingPapers
     *            The training papers
     * @return The calculates TF-IDF score of the word
     */
    public static double calculateTfIdf(String originalWord, Paper containingPaper, List<Paper> trainingPapers) {
        // Prepare for calculations
        String word = sanitiseString(originalWord);

        if (!containingPaper.getTokenCounts().containsKey(word)) {
            // Try the actual word
            word = originalWord.toLowerCase();
            if (!containingPaper.getTokenCounts().containsKey(word)) {
                // Can't handle this as its split differently
                log.error(
                        "TF-IDF calculator can't find \"" + originalWord + "\" in paper " + containingPaper.getTitle());

                // Can't find it, hm...
                // If stop word, go 0, if not stop word, go 1
                return isTokenToIgnore(originalWord) ? 0.0 : 1.0;
            }
        }

        double tf = (double) containingPaper.getTokenCounts().get(word)
                / (double) containingPaper.getTokenCounts().values().stream().reduce(0, Integer::sum);
        int numOfPapersWithTerm = 0;
        for (Paper paperIdf : trainingPapers) {
            if (paperIdf.getTokenCounts().containsKey(word)) {
                numOfPapersWithTerm++;
            }
        }
        // Ensure this paper is counted
        if (!trainingPapers.contains(containingPaper)) {
            numOfPapersWithTerm++;
        }

        double documentCount = trainingPapers.size();
        if (!trainingPapers.contains(containingPaper)) {
            documentCount++;
        }

        double idf = Math.log(documentCount / (double) numOfPapersWithTerm);
        return tf * idf;
    }

    /**
     * Processes key phrases and removes low TF-IDF phrases (threshold = 0.02) which
     * removes some of the noise caused by KP generation
     * 
     * @param kps
     *            The key phrase list to process
     * @param paper
     *            The paper the key phrases are from
     * @param trainingPapers
     *            The training papers used
     */
    public static void removeLowTfIdfKPs(List<KeyPhrase> kps, Paper paper, List<Paper> trainingPapers) {
        List<KeyPhrase> phrasesToRemove = new ArrayList<KeyPhrase>();
        for (KeyPhrase kp : kps) {
            String[] parts = kp.getPhrase().replaceAll(REGEX_ALL_PUNCTUATION, "").split(" ");
            double tfIdf = 0;
            for (String part : parts) {
                tfIdf += NlpUtil.calculateTfIdf(part, paper, trainingPapers);
            }
            if (tfIdf < TF_IDF_THRESHOLD_PHRASE) {
                phrasesToRemove.add(kp);
            }
        }

        // Get rid of rubbish ones
        log.debug("Removing " + phrasesToRemove.size() + " KPs");
        kps.removeAll(phrasesToRemove);
    }

    /**
     * Finds the instance of a token in a sentence
     * 
     * @param token
     *            The token
     * @param sentence
     *            The sentence containing the token
     * @return It's position
     */
    public static int findTokenInstanceInSentence(CoreLabel token, CoreMap sentence) {
        String word = token.get(TextAnnotation.class).toLowerCase();
        int instanceInSentence = 0;
        for (CoreLabel t : sentence.get(TokensAnnotation.class)) {
            token.get(TextAnnotation.class).toLowerCase();
            if (t == token) {
                break;
            } else if (t.get(TextAnnotation.class).toLowerCase().equals(word)) {
                instanceInSentence++;
            }
        }
        return instanceInSentence;
    }

    /**
     * Calculates the depth of a token
     * 
     * @param token
     *            The token to find the depth of
     * @param sentence
     *            The sentence the token is from
     * @return The depth of the token in the sentence's parse tree
     */
    public static Integer calculateTokenParseDepth(CoreLabel token, CoreMap sentence) {
        // First, find out the token depth
        int instanceInSentence = findTokenInstanceInSentence(token, sentence);

        String word = token.get(TextAnnotation.class).toLowerCase();
        Tree tree = sentence.get(TreeAnnotation.class);
        for (Tree subTree : tree.getChildrenAsList()) {
            if (subTree.isLeaf()) {
                return checkLeaf(word, subTree, 1, instanceInSentence);
            } else {
                Integer subCalculation = calculateTokenParseDepth(word, subTree, 1, instanceInSentence);
                if (subCalculation != null) {
                    return subCalculation;
                }
            }
        }

        return null;
    }

    /**
     * Helper for calculating token parse depth
     * 
     * @param token
     *            The token to find the depth of
     * @param tree
     *            The current tree to examine
     * @param depth
     *            The current depth traversed to
     * @param instanceInSentence
     *            The instance of the word in the sentence
     * @return The depth, or null if it cannot be found, or the subtractions (value
     *         < 0) made to the instanceInSentence
     */
    private static Integer calculateTokenParseDepth(String token, Tree tree, int depth, int instanceInSentence) {
        int subtractionsMade = 0;
        for (Tree subTree : tree.getChildrenAsList()) {
            // subTree.pennString(); // (NP (DT The) (NN research) (NN work))
            if (subTree.isLeaf()) {
                return checkLeaf(token, subTree, depth, instanceInSentence);
            } else {
                Integer subCalculation = calculateTokenParseDepth(token, subTree, depth + 1, instanceInSentence);
                if (subCalculation != null) {
                    if (subCalculation < 0) {
                        // Take one off the instance counter and carry on looping
                        instanceInSentence += subCalculation;
                        subtractionsMade += subCalculation;
                    } else {
                        // We found it! Return it!
                        return subCalculation;
                    }
                }
            }
        }

        // Return either the subtractions made (so return value is < 0) or null as
        // nothing to report
        if (subtractionsMade != 0) {
            return subtractionsMade;
        } else {
            return null;
        }
    }

    /**
     * Checks a leaf to see if it is a match and the correct match to a token
     * 
     * @param token
     *            The token
     * @param tree
     *            The tree the token is in
     * @param depth
     *            The current depth of this leaf
     * @param instanceInSentence
     *            The instance counter of the phrsae in the sentence
     * @return The depth, or null if it cannot be found, or the subtractions (value
     *         < 0) made to the instanceInSentence
     */
    private static Integer checkLeaf(String token, Tree tree, int depth, int instanceInSentence) {
        if (tree.nodeString().equalsIgnoreCase(token)) {
            if (instanceInSentence > 0) {
                // We've found a copy of the token
                return -1;
            } else {
                // Otherwise we have found the one we're looking for!
                return depth;
            }
        } else {
            return null;
        }
    }

    /**
     * Calculates the maximum depth of a sentence's parse tree
     * 
     * @param sentence
     *            The sentence the token is from
     * @return The depth of the token in the sentence's parse tree
     */
    public static int calculateSentenceParseDepth(CoreMap sentence) {
        Tree tree = sentence.get(TreeAnnotation.class);

        for (Tree subTree : tree.getChildrenAsList()) {
            if (subTree.isLeaf()) {
                return 1;
            } else {
                int subCalculation = calculateSentenceParseDepth(subTree, 1);
                return subCalculation;
            }
        }

        return 0;
    }

    /**
     * Helper for calculating sentence parse depth
     * 
     * @param tree
     *            The current tree to examine
     * @param depth
     *            The current depth traversed to
     * @return The depth, or null if it cannot be found
     */
    private static Integer calculateSentenceParseDepth(Tree tree, int depth) {
        for (Tree subTree : tree.getChildrenAsList()) {
            if (subTree.isLeaf()) {
                return depth + 1;
            } else {
                return calculateSentenceParseDepth(subTree, depth + 1);
            }
        }
        return depth;
    }

}
