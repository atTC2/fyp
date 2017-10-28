package xyz.tomclarke.fyp.nlp.paper;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Holds useful information about getting text from scientific papers and where
 * they are stored. This is an abstract class as extending classes can support
 * different types of sources (PDF, txt, URI...)
 * 
 * @author tbc452
 *
 */
public abstract class Paper {

    private static final Logger log = LogManager.getLogger(Paper.class);

    private final String location;
    private String text;
    private List<CoreMap> coreNLPAnnotations;

    public Paper(String location) {
        this.location = location;
    }

    /**
     * Gets the text of a paper
     * 
     * @return The text of the paper
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the paper.
     * 
     * @param text
     *            The text of the paper.
     */
    protected void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the location/address of a resource
     * 
     * @return The location of the document
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the annotations created by CoreNLP
     * 
     * @return The annotation information
     */
    public List<CoreMap> getCoreNLPAnnotations() {
        return coreNLPAnnotations;
    }

    /**
     * Sets the annotation information
     * 
     * @param coreNLPAnnotations
     *            The calculated annotations
     */
    public void setCoreNLPAnnotations(List<CoreMap> coreNLPAnnotations) {
        this.coreNLPAnnotations = coreNLPAnnotations;
    }

    /**
     * Prints the annotations
     */
    public void printAnnotations() {
        log.info("Annotation information for " + location + ":");
        for (CoreMap sentence : coreNLPAnnotations) {
            // Print out information on each word
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String pos = token.get(PartOfSpeechAnnotation.class);
                String ne = token.get(NamedEntityTagAnnotation.class);

                log.info("word: " + word + " pos: " + pos + " ne:" + ne);
            }

            // Print out information on the tree
            Tree tree = sentence.get(TreeAnnotation.class);
            log.info("parse tree: " + tree);
        }
    }

}
