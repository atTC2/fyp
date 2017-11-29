package xyz.tomclarke.fyp.nlp.paper.extraction;

/**
 * Represents relationships between key phrases
 * 
 * @author tbc452
 *
 */
public class Relationship extends Extraction {

    private static final long serialVersionUID = -5036452867442789150L;
    private final RelationType type;
    private final KeyPhrase[] phrases;

    public Relationship(int id, RelationType type, KeyPhrase[] phrases) {
        super(id);
        this.type = type;
        this.phrases = phrases;
    }

    @Override
    public String toString() {
        String toString = getPrintId() + Extraction.TAB + type;

        if (type.equals(RelationType.HYPONYM_OF)) {
            toString += Extraction.SPACE + "Arg1:" + phrases[0].getPrintId() + Extraction.SPACE + "Arg2:"
                    + phrases[1].getPrintId();
        } else {
            for (int i = 0; i < phrases.length; i++) {
                toString += Extraction.SPACE + phrases[i];
            }
        }

        return toString;
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
     * Gets the phrases
     * 
     * @return The phrases
     */
    public KeyPhrase[] getPhrases() {
        return phrases;
    }

}
