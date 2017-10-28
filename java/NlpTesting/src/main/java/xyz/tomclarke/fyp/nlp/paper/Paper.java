package xyz.tomclarke.fyp.nlp.paper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
public abstract class Paper implements Serializable {

    private static final long serialVersionUID = 8181333621254995423L;
    private static final Logger log = LogManager.getLogger(Paper.class);
    protected static final String SER_FILE_EXT = ".ser";

    private final String location;
    private final String serLocation;
    private String text;
    private List<CoreMap> coreNLPAnnotations;

    public Paper(String location) {
        this.location = location;

        // Set up the serialise file location
        // TODO support none local file saves
        serLocation = location + SER_FILE_EXT;

        // Attempt to load in the file if it already exists
        if (new File(serLocation).exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serLocation));
                Paper loadedPaper = (Paper) ois.readObject();
                ois.close();

                // Set the necessary parameters
                text = loadedPaper.getText();
                coreNLPAnnotations = loadedPaper.getCoreNLPAnnotations();
            } catch (IOException | ClassNotFoundException e) {
                log.error("Error reading serialisation", e);
            }
        }
    }

    /**
     * Saves the object to disk
     */
    private void save() {
        try {
            // Get rid of the old file if it's there
            File oldSer = new File(serLocation);
            if (oldSer.exists()) {
                oldSer.delete();
            }

            // Write new serialisation
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serLocation));
            oos.writeObject(this);
            oos.close();
        } catch (IOException e) {
            log.error("Error writing serialisation", e);
        }
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
        save();
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
        save();
    }

    /**
     * Prints the annotations
     */
    public void printAnnotations() {
        if (coreNLPAnnotations != null && !coreNLPAnnotations.isEmpty()) {
            log.debug("Annotation information for " + location + ":");
            for (CoreMap sentence : coreNLPAnnotations) {
                // Print out information on each word
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    String word = token.get(TextAnnotation.class);
                    String pos = token.get(PartOfSpeechAnnotation.class);
                    String ne = token.get(NamedEntityTagAnnotation.class);

                    log.debug("word: " + word + " pos: " + pos + " ne:" + ne);
                }

                // Print out information on the tree
                Tree tree = sentence.get(TreeAnnotation.class);
                log.debug("parse tree: " + tree);
            }
        } else {
            log.warn("No annotations available for " + location);
        }
    }

}
