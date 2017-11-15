package xyz.tomclarke.fyp.nlp.paper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import xyz.tomclarke.fyp.nlp.keyphrase.Classification;
import xyz.tomclarke.fyp.nlp.keyphrase.Extraction;
import xyz.tomclarke.fyp.nlp.keyphrase.KeyPhrase;
import xyz.tomclarke.fyp.nlp.keyphrase.Position;

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
    protected static final String ANN_FILE_EXT = ".ann";

    private final String location;
    private final String serLocation;
    private String text;
    private List<CoreMap> coreNLPAnnotations;
    private List<Extraction> extractions;
    private Map<String, Integer> tokenCounts;

    public Paper(String location, boolean canAttemptAnnRead) {
        this.location = location;
        extractions = new ArrayList<Extraction>();
        tokenCounts = new HashMap<String, Integer>();

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
                extractions = loadedPaper.getExtractions();
                tokenCounts = loadedPaper.getTokenCounts();
            } catch (IOException | ClassNotFoundException e) {
                log.error("Error reading serialisation", e);
            }

            // We've loaded in the completed file (so far) so do not load in further
            // information.
            return;
        }

        // Attempt to file existing annotation data
        if (canAttemptAnnRead) {
            // Try and read in same name .ann
            String annLocation = location.replace(".txt", ANN_FILE_EXT);
            loadAnnFile(annLocation);
        }
    }

    /**
     * Use an annotation file to add already extracted key phrase information to the
     * file.
     * 
     * @param annLocation
     *            The location of the annotation file.
     */
    public void loadAnnFile(String annLocation) {
        File annFile = new File(annLocation);
        if (annFile.exists()) {
            try (Scanner scanner = new Scanner(annFile)) {
                while (scanner.hasNextLine()) {
                    Extraction newExt = Extraction.createExtractionFromString(scanner.nextLine(), extractions);
                    addExtraction(newExt);
                }
            } catch (IOException e) {
                log.error("Problem reading papers.txt", e);
            }
        }
    }

    /**
     * Generates a new key phrase. The phrase is what is in between the two indexes
     * given, blank space trimmed
     * 
     * @param start
     *            The start of the key phrase
     * @param end
     *            The end of the key phrase
     * @return The new key phrase object (with set ID and unknown classification)
     * @throws Exception
     *             If the phrase is empty
     */
    public KeyPhrase makeKeyPhrase(int start, int end) throws Exception {
        int lowestAvailableId = 0;

        // Find the highest ID and add one
        for (Extraction ext : extractions) {
            if (lowestAvailableId <= ext.getId()) {
                lowestAvailableId = ext.getId() + 1;
            }
        }

        String phrase = text.substring(start, end).trim();

        // If it's a mistake, it could be a blank line...
        if (phrase.isEmpty()) {
            throw new Exception("Generated Key Phrase is empty");
        }

        // Cater for shortening at the start of the phrase
        start = start + text.substring(start).indexOf(phrase.charAt(0));

        return new KeyPhrase(lowestAvailableId, phrase, new Position(start, start += phrase.length()),
                Classification.UNKNOWN);
    }

    /**
     * Tests whether the token selected is part of a key phrase
     * 
     * TODO import this
     * 
     * @param token
     *            The token to check
     * @return Whether the token is part of a key phrase
     */
    public boolean isTokenPartOfKeyPhrase(CoreLabel token) {
        String word = token.get(TextAnnotation.class).toLowerCase();
        for (Extraction phrase : getExtractions()) {
            if (phrase instanceof KeyPhrase) {
                String phraseWord = ((KeyPhrase) phrase).getPhrase().toLowerCase();
                // Word must be start, end, equal or token of key phrase
                if (phraseWord.startsWith(word) || phraseWord.endsWith(word) || phraseWord.equals(word)
                        || phraseWord.contains(" " + word + " ")) {
                    return true;
                }
            }
        }
        return false;
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
     * Gets the sentence containing a given token
     * 
     * @param targetToken
     *            The token to find the parent sentence of
     * @return The parent sentence.
     */
    public CoreMap getSentenceWithToken(CoreLabel targetToken) {
        for (CoreMap sentence : coreNLPAnnotations) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                if (token == targetToken) {
                    return sentence;
                }
            }
        }

        // Could not find a parent sentence.
        return null;
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

        // Calculate token counts
        for (CoreMap sentence : coreNLPAnnotations) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class).toLowerCase();

                if (!tokenCounts.containsKey(word)) {
                    // Not already seen, add
                    tokenCounts.put(word, 1);
                } else {
                    // Already seen, add
                    tokenCounts.put(word, tokenCounts.get(word) + 1);
                }
            }
        }

        save();
    }

    /**
     * Gets the papers key phrase information
     * 
     * @return The key phrase information
     */
    public List<Extraction> getExtractions() {
        return extractions;
    }

    /**
     * Sets the key phrase information
     * 
     * @param keyPhrasesExtractions
     *            The new key phrase information
     */
    public void setExtractions(List<Extraction> keyPhrasesExtractions) {
        this.extractions = keyPhrasesExtractions;
    }

    /**
     * Adds a key phrase to this paper
     * 
     * @param keyPhraseExtraction
     *            The phrase to add
     */
    public void addExtraction(Extraction keyPhraseExtraction) {
        extractions.add(keyPhraseExtraction);
    }

    /**
     * Gets the token counts
     * 
     * @return Gets the token counts
     */
    public Map<String, Integer> getTokenCounts() {
        return tokenCounts;
    }

    /**
     * Sets the token counts
     * 
     * @param tokenCounts
     *            The token counts
     */
    public void setTokenCounts(Map<String, Integer> tokenCounts) {
        this.tokenCounts = tokenCounts;
        save();
    }

    /**
     * Prints the annotations
     */
    public void printAnnotations() {
        if (coreNLPAnnotations != null && !coreNLPAnnotations.isEmpty()) {
            log.debug("Annotation information for " + location + ":");
            for (CoreMap sentence : coreNLPAnnotations) {
                // Print out the original sentence
                String originalSentence = sentence.get(TextAnnotation.class);
                log.debug("original sentence: " + originalSentence);

                // Print out information on the tree
                Tree tree = sentence.get(TreeAnnotation.class);
                log.debug("parse tree: " + tree);

                // Print out information on each word
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    String word = token.get(TextAnnotation.class);
                    String pos = token.get(PartOfSpeechAnnotation.class);

                    log.debug("word: " + word + " pos: " + pos);
                }
            }
        } else {
            log.warn("No annotations available for " + location);
        }
    }

    /**
     * Prints key phrase information
     */
    public void printKeyPhraseInformation() {
        if (!extractions.isEmpty()) {
            log.debug("Key phrase information for " + location + ":");
            log.debug(getExtractionString());
        } else {
            log.warn("No key phrase information for " + location);
        }
    }

    /**
     * Creates printable extraction information for this paper.
     * 
     * @return Extraction information
     */
    public String getExtractionString() {
        String returnString = "";

        if (!extractions.isEmpty()) {
            for (Extraction extraction : extractions) {
                returnString += extraction.toString();
                returnString += System.lineSeparator();
            }
        }

        return returnString;
    }

    @Override
    public String toString() {
        int keyPhraseCount = 0;
        int relationshipCount = 0;

        for (Extraction extraction : extractions) {
            if (extraction instanceof KeyPhrase) {
                keyPhraseCount++;
            } else {
                relationshipCount++;
            }
        }

        return location + " (" + keyPhraseCount + " keyphrases found, " + relationshipCount + " relationships found, "
                + tokenCounts.size() + " different tokens, " + tokenCounts.values().stream().reduce(0, Integer::sum)
                + " total tokens)";
    }

}
