package xyz.tomclarke.fyp.nlp.paper.extraction;

/**
 * Represents the possible types of key phrases
 * 
 * @author tbc452
 *
 */
public enum Classification {

    UNKNOWN("Unknown"), PROCESS("Process"), TASK("Task"), MATERIAL("Material");

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
        if (description == null) {
            return UNKNOWN;
        } else if (description.equals(PROCESS.description)) {
            return PROCESS;
        } else if (description.equals(TASK.description)) {
            return TASK;
        } else if (description.equals(MATERIAL.description)) {
            return MATERIAL;
        }
        return UNKNOWN;
    }
}
