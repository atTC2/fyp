package xyz.tomclarke.fyp.nlp.paper.extraction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the possible types of key phrases
 * 
 * @author tbc452
 *
 */
public enum Classification {

    UNKNOWN("Unknown"), PROCESS("Process"), TASK("Task"), MATERIAL("Material");

    private static final Logger log = LogManager.getLogger(Classification.class);
    private String description;

    private Classification(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Get a classification type from a string description
     * 
     * @param description
     *            The description
     * @return The found classification type
     */
    public static Classification getClazz(String description) {
        if (description.equals(PROCESS.description)) {
            return PROCESS;
        } else if (description.equals(TASK.description)) {
            return TASK;
        } else if (description.equals(MATERIAL.description)) {
            return MATERIAL;
        } else if (description.equals(UNKNOWN.description)) {
            return UNKNOWN;
        }

        log.warn("Could not load Classification: " + description);
        return UNKNOWN;
    }
}
