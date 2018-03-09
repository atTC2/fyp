package xyz.tomclarke.fyp.nlp.paper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import xyz.tomclarke.fyp.nlp.paper.extraction.Classification;
import xyz.tomclarke.fyp.nlp.paper.extraction.Extraction;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.extraction.Position;
import xyz.tomclarke.fyp.nlp.paper.extraction.RelationType;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;
import xyz.tomclarke.fyp.nlp.util.NlpError;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Holds useful information about getting text from scientific papers and where
 * they are stored. This is an abstract class as extending classes can support
 * different types of sources (PDF, txt, URI...)
 * 
 * @author tbc452
 *
 */
public abstract class Paper implements Serializable {

    private static final long serialVersionUID = -6589675606604453669L;
    private static final Logger log = LogManager.getLogger(Paper.class);
    protected static final String SER_FILE_EXT = ".ser";
    protected static final String ANN_FILE_EXT = ".ann";

    private final String location;
    private final String serLocation;
    private final boolean saveUpdatedToDisk;
    private String text;
    private List<CoreMap> annotations;
    private List<Extraction> extractions;
    private Map<String, Integer> tokenCounts;
    private String title;
    private String author;

    public Paper(String location, boolean canAttemptAnnRead, boolean saveUpdatedToDisk) {
        this.location = location;
        this.saveUpdatedToDisk = saveUpdatedToDisk;
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
                annotations = loadedPaper.getAnnotations();
                if (canAttemptAnnRead) {
                    extractions = loadedPaper.getExtractions();
                }
                tokenCounts = loadedPaper.getTokenCounts();
                title = loadedPaper.getTitle();
                author = loadedPaper.getAuthor();
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
     * given, blank space trimmed and the key phrase sanitised
     * 
     * @param start
     *            The start of the key phrase
     * @param end
     *            The end of the key phrase
     * @param clazz
     *            The classification being found
     * @param trainingPapers
     *            The training papers available, used in sanitisation
     * @return The new key phrase object (with set ID and unknown classification)
     * @throws NlpError
     */
    public KeyPhrase makeKeyPhrase(int start, int end, Classification clazz, List<Paper> trainingPapers)
            throws NlpError {
        String originalKp = text.substring(start, end);

        // Find the highest ID and add one
        int id = getNextKpId();

        String phrase = text.substring(start, end);

        // If it's a mistake, it could be a blank line...
        if (phrase.isEmpty()) {
            throw new NlpError("Generated Key Phrase is empty");
        }

        // Sanitise!
        boolean sanitising = true;
        // Work on beginning of line
        while (sanitising) {
            int nextSpaceIndex = phrase.indexOf(" ");
            if (nextSpaceIndex == -1) {
                // No more spaces
                sanitising = false;
            } else if (nextSpaceIndex == 0) {
                // It is a space
                start++;
                phrase = phrase.substring(1);
            } else {
                String token = phrase.substring(0, nextSpaceIndex);
                if (!isTokenSuitableForKeyPhraseEdge(token, trainingPapers)) {
                    // Remove it, and the following space
                    phrase = phrase.substring(nextSpaceIndex + 1);
                    start += nextSpaceIndex + 1;
                } else {
                    // Happy with the first term of the phrase
                    sanitising = false;
                }
            }
            // Remove starting stop things
            if (phrase.length() > 2 && NlpUtil.isTokenToIgnore(phrase.substring(0, 1))) {
                start++;
                phrase = phrase.substring(1);
            }
        }
        // Work on ending of line
        sanitising = true;
        while (sanitising) {
            int lastSpaceIndex = phrase.lastIndexOf(" ");
            if (lastSpaceIndex == -1) {
                // No more spaces
                sanitising = false;
            } else if (lastSpaceIndex == phrase.length() + 1) {
                // It is a space
                end--;
                phrase = phrase.substring(0, phrase.length() - 1);
            } else {
                String token = phrase.substring(lastSpaceIndex + 1);
                if (!isTokenSuitableForKeyPhraseEdge(token, trainingPapers)) {
                    // Remove it, and the following space
                    phrase = phrase.substring(0, lastSpaceIndex);
                    end = start + phrase.length();
                } else {
                    // Happy with the end term of the phrase
                    sanitising = false;
                }
            }
            // Remove ending stop things
            if (phrase.length() > 2 && NlpUtil.isTokenToIgnore(phrase.substring(phrase.length() - 1))) {
                end--;
                phrase = phrase.substring(0, phrase.length() - 1);
            }
        }

        // Sometimes, a partial reference hangs onto the key phrase... kill it
        if (phrase.matches(".*[(][A-Z][a-z]+")) {
            int split = phrase.lastIndexOf("(");
            phrase = phrase.substring(0, split).trim();
            end = start + phrase.length();
        }
        if (phrase.matches(".*\\[[0-9,]+]?")) {
            int split = phrase.lastIndexOf("[");
            phrase = phrase.substring(0, split).trim();
            end = start + phrase.length();
        }

        // Final checks
        if (phrase.isEmpty()) {
            throw new NlpError("Generated Key Phrase is empty. Original: \"" + originalKp + "\"");
        } else if (!phrase.matches(".*[a-zA-Z0-9αρΛ≡ε∞].*")) {
            throw new NlpError("Generated Key Phrase has no real content: \"" + phrase + "\"");
        }

        return new KeyPhrase(id, phrase, new Position(start, start += phrase.length()), clazz);
    }

    /**
     * Scans for easy wins to get relationships from key phrases. New key phrases
     * and changes made will be automatically made to the paper object. If a custom
     * list of key phrases are given, changes are not committed
     * 
     * @param kps
     *            Custom KPs to do this processing on (rather than this papers one)
     *            - for testing purposes
     * @return New or changed extractions
     */
    public List<Extraction> makeEasyWinsFromKeyPhrases(List<KeyPhrase> kps) {
        List<Extraction> exts = new ArrayList<Extraction>();
        List<Extraction> extsToRemove = new ArrayList<Extraction>();
        int nextKpId = getNextKpId();
        int nextRelId = getNextRelId();

        boolean operatingOnThisPaper = kps == null;
        if (operatingOnThisPaper) {
            kps = getKeyPhrases();
        }

        /*
         * Easy win 1: synonyms based on acronyms
         */
        for (KeyPhrase kp : kps) {
            boolean isKpContainsAcronym = false;
            String phrase = kp.getPhrase();
            if (phrase.contains("(")) {
                String subPhrase = phrase.substring(0, phrase.indexOf("("));
                subPhrase = subPhrase.replaceAll("[^A-Z]", "");
                String subPhraseAcronym = phrase.substring(phrase.indexOf("("));
                if (subPhraseAcronym.contains(")")) {
                    subPhraseAcronym = subPhraseAcronym.substring(0, subPhraseAcronym.indexOf(")"));
                }
                subPhraseAcronym = subPhraseAcronym.replaceAll("[^A-Z]", "");
                isKpContainsAcronym = !subPhrase.isEmpty() && !subPhraseAcronym.isEmpty()
                        && (subPhrase.contains(subPhraseAcronym) || subPhraseAcronym.contains(subPhrase));
            }

            if (isKpContainsAcronym) {
                log.info("Splitting phrase \"" + phrase + "\" to make acronym (synonym relation)");
                // Split the original kp, make a new kp, and draw a synonym relationship between
                // them
                // Get the main KP
                String mainPhrase = phrase.substring(0, phrase.indexOf("(")).trim();
                Position pos = new Position(kp.getPosition().getStart(),
                        kp.getPosition().getStart() + mainPhrase.length());
                KeyPhrase mainKp = new KeyPhrase(kp.getId(), mainPhrase, pos, kp.getClazz());

                // Get the acronym
                String acrPhrase = phrase.substring(phrase.indexOf("(") + 1).trim();
                if (acrPhrase.contains(")")) {
                    acrPhrase = acrPhrase.substring(0, acrPhrase.indexOf(")")).trim();
                }
                int acrStart = pos.getStart() + phrase.indexOf(acrPhrase);
                Position acrPos = new Position(acrStart, acrStart + acrPhrase.length());
                KeyPhrase acrKp = new KeyPhrase(nextKpId, acrPhrase, acrPos, kp.getClazz());

                // Make the relationship
                KeyPhrase[] relKps = new KeyPhrase[2];
                relKps[0] = mainKp;
                relKps[1] = acrKp;
                Relationship rel = new Relationship(nextRelId, RelationType.SYNONYM_OF, relKps);

                // Queue old KP for removal
                extsToRemove.add(kp);

                // Add them to return for informative purposes
                exts.add(mainKp);
                exts.add(acrKp);
                exts.add(rel);

                // Increment IDs for future things
                nextKpId++;
                nextRelId++;
            }
        }

        /**
         * Commit all changes (unless testing)
         */
        if (operatingOnThisPaper) {
            // Remove old KPs
            for (Extraction ext : extsToRemove) {
                extractions.remove(ext);
            }
            // Add new ones
            for (Extraction ext : exts) {
                extractions.add(ext);
            }
        }

        return exts;
    }

    /**
     * Get the next available key phrase ID
     * 
     * @return The next key phrase ID
     */
    private int getNextKpId() {
        int lowestAvailableId = 0;
        for (KeyPhrase kp : getKeyPhrases()) {
            if (lowestAvailableId <= kp.getId()) {
                lowestAvailableId = kp.getId() + 1;
            }
        }
        return lowestAvailableId;
    }

    /**
     * Get the next available relation ID
     * 
     * @return The next relation ID
     */
    private int getNextRelId() {
        int lowestAvailableId = 0;
        for (Relationship rel : getRelationships()) {
            if (lowestAvailableId <= rel.getId()) {
                lowestAvailableId = rel.getId() + 1;
            }
        }
        return lowestAvailableId;
    }

    /**
     * Checks to see if the token should be included in the key phrase or not
     * (intended for edge cases)
     * 
     * @param token
     *            The token to test
     * @param trainingPapers
     *            The training papers available
     * @return If it should be allowed
     */
    private boolean isTokenSuitableForKeyPhraseEdge(String token, List<Paper> trainingPapers) {
        // Filter simple things
        if (token == null || token.isEmpty() || token.equals(" ")) {
            return false;
        }

        // Stop word/bad symbol?
        if (NlpUtil.isTokenToIgnore(token)) {
            return false;
        }

        // Good enough TF-IDF?
        if (NlpUtil.calculateTfIdf(token, this, trainingPapers) < NlpUtil.TF_IDF_THRESHOLD_TOKEN) {
            return false;
        }

        // Made it this far
        return true;
    }

    /**
     * Tests whether the token selected is part of a key phrase
     * 
     * @param token
     *            The token to check
     * @return Whether the token is part of a key phrase
     */
    public boolean isTokenPartOfKeyPhrase(CoreLabel token) {
        return isTokenPartOfKeyPhrase(token, null);
    }

    /**
     * Tests whether the token selected is part of a key phrase of a certain
     * classification
     * 
     * @param token
     *            The token to check
     * @param clazz
     *            The classification to match against (null if searching for general
     *            key phrase)
     * @return Whether the token is part of a key phrase
     */
    public boolean isTokenPartOfKeyPhrase(CoreLabel token, Classification clazz) {
        String word = token.get(TextAnnotation.class).toLowerCase();
        for (KeyPhrase phrase : getKeyPhrases()) {
            String phraseWord = ((KeyPhrase) phrase).getPhrase().toLowerCase();
            // Word must be the phrase or a token of the phrase
            if (phraseWord.equals(word) || Arrays.asList(phraseWord.split(" ")).contains(word)) {
                // Check classification
                if (clazz == null || ((KeyPhrase) phrase).getClazz().equals(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Saves the object to disk (if in ann mode)
     */
    private void save() {
        if (saveUpdatedToDisk) {
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
    public List<CoreMap> getAnnotations() {
        return annotations;
    }

    /**
     * Sets the annotation information
     * 
     * @param annotations
     *            The calculated annotations
     */
    public void setAnnotations(List<CoreMap> annotations) {
        this.annotations = annotations;

        // Calculate token counts
        for (CoreMap sentence : annotations) {
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
     * Gets specifically key phrase extractions
     * 
     * @return Key phrase extractions
     */
    public List<KeyPhrase> getKeyPhrases() {
        List<KeyPhrase> kps = new ArrayList<KeyPhrase>();
        for (Extraction ext : extractions) {
            if (ext instanceof KeyPhrase) {
                kps.add((KeyPhrase) ext);
            }
        }
        return kps;
    }

    /**
     * Gets specifically relation extractions
     * 
     * @return Relation extractions
     */
    public List<Relationship> getRelationships() {
        List<Relationship> rels = new ArrayList<Relationship>();
        for (Extraction ext : extractions) {
            if (ext instanceof Relationship) {
                rels.add((Relationship) ext);
            }
        }
        return rels;
    }

    /**
     * Sets the key phrase information
     * 
     * @param keyPhrasesExtractions
     *            The new key phrase information
     */
    public void setExtractions(List<Extraction> keyPhrasesExtractions) {
        this.extractions = keyPhrasesExtractions;
        save();
    }

    /**
     * Adds a key phrase to this paper
     * 
     * @param keyPhraseExtraction
     *            The phrase to add
     */
    public void addExtraction(Extraction keyPhraseExtraction) {
        extractions.add(keyPhraseExtraction);
        save();
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
     * Gets the title of the paper
     * 
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the paper
     * 
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        save();
    }

    /**
     * Gets the author of the paper
     * 
     * @return
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the paper
     * 
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
        save();
    }

    /**
     * Prints the annotations
     */
    public void printAnnotations() {
        if (annotations != null && !annotations.isEmpty()) {
            log.debug("Annotation information for " + location + ":");
            for (CoreMap sentence : annotations) {
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
