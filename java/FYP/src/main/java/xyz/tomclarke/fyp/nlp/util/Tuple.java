package xyz.tomclarke.fyp.nlp.util;

/**
 * To hold a pairing of information
 * 
 * @author tbc452
 * @param <S>
 *            The type of the key
 * @param <T>
 *            The type of the value
 *
 */
public class Tuple<S, T> {

    private S key;
    private T value;

    public Tuple() {
        // Empty constructor
    }

    public Tuple(S key, T value) {
        this.key = key;
        this.value = value;
    }

    public S getKey() {
        return key;
    }

    public void setKey(S key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
