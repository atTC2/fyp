package xyz.tomclarke.fyp.nlp.paper.extraction;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents an extracted piece of information
 * 
 * @author tbc452
 *
 */
public abstract class Extraction implements Serializable {

    private static final long serialVersionUID = 3195125856271889107L;
    private static final Logger log = LogManager.getLogger(Extraction.class);

    public static final String TAB = "\t";
    public static final String SPACE = " ";

    // When relationship and synonym-of, ID = 0
    private final int id;

    public Extraction(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the extracted information
     * 
     * @return The ID of the extracted information
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the ID with a character at the front to denote the type of extracted
     * information
     * 
     * @return The ID as a string
     */
    public abstract String getPrintId();

    /**
     * Creates an extraction object by parsing saved extraction text data:
     * KeyPhraes: T< id >\t< Classification > < start pos > < end pos >\t< phrase >
     * Relationship: R< id >\t< RelationType > Arg1:< phrase1 > Arg2:< phrase2 >
     * 
     * @param extraction
     *            The string holding the extraction information
     * @param keyPhrasesExtractions
     *            Already found key phrases (so they can be linked in relationships)
     * @return The extraction object, or null if reading in the line was not
     *         successful.
     */
    public static Extraction createExtractionFromString(String extraction, List<Extraction> keyPhrasesExtractions) {
        if (extraction == null || extraction.isEmpty()) {
            return null;
        } else {
            String[] parts = extraction.split(TAB);
            // Phrase should have 3 parts, relations should have 2
            switch (parts.length) {
            case 3:
                // KeyPhrase
                String[] phraseParts = parts[1].split(SPACE);
                if (phraseParts.length != 3) {
                    // Should have 3 parts
                    break;
                }
                return new KeyPhrase(Integer.valueOf(parts[0].substring(1)), parts[2], Integer.valueOf(phraseParts[1]),
                        Integer.valueOf(phraseParts[2]), Classification.getClazz(phraseParts[0]));
            case 2:
                // Relationship
                String[] relationParts = parts[1].split(SPACE);
                if (relationParts.length < 3) {
                    // Should have at least 3 parts
                    break;
                }

                KeyPhrase[] phrases;

                // Get the type of ID, that'll help a lot
                int id;
                RelationType type;
                if (parts[0].equals(RelationType.SYNONYM_OF.getId(0))) {
                    // Going to be a synonym
                    id = 0;
                    type = RelationType.SYNONYM_OF;
                    // There could be 2 or more...
                    phrases = new KeyPhrase[relationParts.length - 1];
                    for (int i = 0; i < phrases.length; i++) {
                        phrases[i] = findKeyPhraseObject("", relationParts[i + 1], keyPhrasesExtractions);
                    }
                } else {
                    // Going to be a hyponym
                    id = Integer.valueOf(parts[0].substring(1));
                    type = RelationType.HYPONYM_OF;
                    phrases = new KeyPhrase[2];
                    phrases[0] = findKeyPhraseObject("Arg1:", relationParts[1], keyPhrasesExtractions);
                    phrases[1] = findKeyPhraseObject("Arg2:", relationParts[2], keyPhrasesExtractions);
                }

                // Make sure we got phrases (or at least we have 2 - the minimum for a
                // relationship)
                if (phrases[0] == null || phrases[1] == null) {
                    break;
                }

                return new Relationship(id, type, phrases);
            }
        }

        log.error("Unable to parse extraction: " + extraction);
        return null;
    }

    /**
     * Finds the key phrase associated with the string ID given
     * 
     * @param idPrefix
     *            The prefix of the ID
     * @param ref
     *            The ID reference to check (for a match)
     * @param keyPhrasesExtractions
     *            Already found key phrases (so they can be linked in relationships)
     * @return The key phrase, or null if one cannot be found
     */
    private static KeyPhrase findKeyPhraseObject(String idPrefix, String ref, List<Extraction> keyPhrasesExtractions) {
        // Find the key phrases
        for (Extraction ext : keyPhrasesExtractions) {
            // Ensure it is a KeyPhrase
            KeyPhrase phrase;
            if (ext instanceof KeyPhrase) {
                phrase = (KeyPhrase) ext;
            } else {
                continue;
            }

            if ((idPrefix + phrase.getPrintId()).equals(ref)) {
                return phrase;
            }
        }
        return null;
    }
}
