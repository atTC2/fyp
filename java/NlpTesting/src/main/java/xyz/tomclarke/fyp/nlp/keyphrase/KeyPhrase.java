package xyz.tomclarke.fyp.nlp.keyphrase;

/**
 * Represents a discovered key phrase
 * 
 * @author tbc452
 *
 */
public class KeyPhrase extends Extraction {

    private static final long serialVersionUID = 6096698722378759140L;
    private final String phrase;
    private final Position position;
    private Classification clazz;

    public KeyPhrase(int id, String phrase, Position position, Classification clazz) {
        super(id);
        this.phrase = phrase;
        this.position = position;
        this.clazz = clazz;
    }

    public KeyPhrase(int id, String phrase, int posStart, int posEnd, Classification clazz) {
        super(id);
        this.phrase = phrase;
        this.position = new Position(posStart, posEnd);
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return getPrintId() + Extraction.TAB + clazz + Extraction.SPACE + position.getStart() + Extraction.SPACE
                + position.getEnd() + Extraction.TAB + phrase;
    }

    @Override
    public String getPrintId() {
        return "T" + getId();
    }

    /**
     * Gets the key phrase
     * 
     * @return The phrase
     */
    public String getPhrase() {
        return phrase;
    }

    /**
     * Gets the position of the phrase of the original text
     * 
     * @return The position of the phrase
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the classification of the phrase
     * 
     * @return The classification type
     */
    public Classification getClazz() {
        return clazz;
    }

    /**
     * Sets the classification of the phrase
     * 
     * @param clazz
     *            The classification type
     */
    public void setClazz(Classification clazz) {
        this.clazz = clazz;
    }
}
