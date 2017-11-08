package xyz.tomclarke.fyp.nlp.keyphrase;

/**
 * Represents relationships between key phrases
 * 
 * @author tbc452
 *
 */
public class Relationship extends Extraction {

    private static final long serialVersionUID = -5036452867442789150L;
    private final RelationType type;
    private final KeyPhrase phrase1;
    private final KeyPhrase phrase2;

    public Relationship(int id, RelationType type, KeyPhrase phrase1, KeyPhrase phrase2) {
        super(id);
        this.type = type;
        this.phrase1 = phrase1;
        this.phrase2 = phrase2;
    }

    @Override
    public String toString() {
        return getPrintId() + Extraction.TAB + type + Extraction.SPACE + type.getKp1Prefix() + phrase1.getPrintId()
                + Extraction.SPACE + type.getKp2Prefix() + phrase2.getPrintId();
    }

    @Override
    public String getPrintId() {
        return type.getId(getId());
    }

    /**
     * Gets the type of relationship
     * 
     * @return The relationship type
     */
    public RelationType getType() {
        return type;
    }

    /**
     * Gets the first phrase
     * 
     * @return The first phrase
     */
    public KeyPhrase getPhrase1() {
        return phrase1;
    }

    /**
     * Gets the second phrase
     * 
     * @return The second phrase
     */
    public KeyPhrase getPhrase2() {
        return phrase2;
    }

}
