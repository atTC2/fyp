package xyz.tomclarke.fyp.nlp.keyphrase;

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
     * @param paper
     *            The paper the key phrases are associated with, as they may need to
     *            be retrieved to create relationships
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
                if (relationParts.length != 3) {
                    // Should have 3 parts
                    break;
                }

                // Get the type of ID, that'll help a lot
                int id;
                RelationType type;
                if (parts[0].equals(RelationType.SYNONYM_OF.getId(0))) {
                    // Going to be a synonym
                    id = 0;
                    type = RelationType.SYNONYM_OF;
                } else {
                    // Going to be a hyponym
                    id = Integer.valueOf(parts[0].substring(1));
                    type = RelationType.HYPONYM_OF;
                }

                // Find the key phrases
                KeyPhrase phrase1 = null;
                KeyPhrase phrase2 = null;
                for (Extraction ext : keyPhrasesExtractions) {
                    // Ensure it is a KeyPhrase
                    KeyPhrase phrase;
                    if (ext instanceof KeyPhrase) {
                        phrase = (KeyPhrase) ext;
                    } else {
                        continue;
                    }

                    if ((type.getKp1Prefix() + phrase.getPrintId()).equals(relationParts[1])) {
                        phrase1 = phrase;
                        continue;
                    }
                    if ((type.getKp2Prefix() + phrase.getPrintId()).equals(relationParts[2])) {
                        phrase2 = phrase;
                    }
                }

                // Make sure we got phrases
                if (phrase1 == null || phrase2 == null) {
                    break;
                }

                return new Relationship(id, type, phrase1, phrase2);
            }
        }

        log.warn("Unable to parse extraction: " + extraction);
        return null;
    }
}
