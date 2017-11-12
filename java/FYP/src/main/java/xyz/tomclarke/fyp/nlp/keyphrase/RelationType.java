package xyz.tomclarke.fyp.nlp.keyphrase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the possible types of relations
 * 
 * @author tbc452
 *
 */
public enum RelationType {

    HYPONYM_OF("Hyponym-of", "R%"), SYNONYM_OF("Synonym-of", "*");

    private static final Logger log = LogManager.getLogger(RelationType.class);

    private final String description;
    private final String idFormat;

    private RelationType(String description, String idFormat) {
        this.description = description;
        this.idFormat = idFormat;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Gets the ID formatted correctly for this type of relation
     * 
     * @param id
     *            The ID number
     * @return The ID string
     */
    public String getId(int id) {
        return String.format(idFormat, id);
    }

    /**
     * Get a relationship type from a string description
     * 
     * @param description
     *            The description
     * @return The found relationship type (null if nothing found)
     */
    public static RelationType getRelation(String description) {
        if (description.equals(HYPONYM_OF.description)) {
            return HYPONYM_OF;
        } else if (description.equals(SYNONYM_OF.description)) {
            return SYNONYM_OF;
        }

        log.warn("Could not load Classification: " + description);
        return null;
    }
}
