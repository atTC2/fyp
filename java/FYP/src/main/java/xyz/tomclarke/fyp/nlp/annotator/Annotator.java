package xyz.tomclarke.fyp.nlp.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import xyz.tomclarke.fyp.nlp.util.Tuple;

/**
 * Can be used to annotate sentences or phrases
 * 
 * @author tbc452
 *
 */
public class Annotator {

    private StanfordCoreNLP pipeline;

    public Annotator() {
        // Create the CoreNLP pipeline for processing
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);
    }

    /**
     * Annotates text
     * 
     * @param text
     *            The text to annotate
     * @return The sentence annotation
     */
    public List<CoreMap> annotate(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        return document.get(SentencesAnnotation.class);
    }

    /**
     * Gets all base phrases for a sentence
     * 
     * @param tree
     *            The tree
     * @return A list of base phrases
     */
    public List<Tree> getLowestNPStrings(Tree tree) {
        List<Tree> nps = new ArrayList<Tree>();

        for (Tree child : tree.getChildrenAsList()) {
            if (child.label().value().equals("NP") && !hasNPChild(child, true)) {
                // Child is NP and doesn't have child NP at any point
                nps.add(child);
            } else {
                nps.addAll(getLowestNPStrings(child));
            }
        }

        return nps;
    }

    /**
     * Goes through tree and finds if there is an NP somewhere in there
     * 
     * @param tree
     *            The tree to iterate
     * @param isRoot
     *            Is it the root of the tested tree
     * @return If there is a noun phrase further down the tree
     */
    private boolean hasNPChild(Tree tree, boolean isRoot) {
        // Check self
        if (tree.label().value().equals("NP") && !isRoot) {
            return true;
        }

        // Check children
        for (Tree child : tree.getChildrenAsList()) {
            if (hasNPChild(child, false)) {
                return true;
            }
        }

        // No more NPs
        return false;

    }

    /**
     * Gets the deepest noun (or last if same depth)
     * 
     * @param tree
     * @return The string of the deepest noun and it's depth in the parse tree
     */
    public Tuple<String, Integer> getDeepestNoun(Tree tree, int depth) {
        boolean isN = tree.label().value().startsWith("NN");
        if (isN) {
            return new Tuple<String, Integer>(tree.getChild(0).label().value(), depth);
        }

        Tuple<String, Integer> deepestSoFar = new Tuple<String, Integer>(null, 0);
        for (Tree child : tree.getChildrenAsList()) {
            Tuple<String, Integer> pair = getDeepestNoun(child, depth + 1);
            if (pair.getValue() >= deepestSoFar.getValue()) {
                deepestSoFar = pair;
            }
        }

        return deepestSoFar;
    }

}
