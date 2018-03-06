package xyz.tomclarke.fyp.nlp.util;

/**
 * Custom NLP errors
 * 
 * @author tbc452
 *
 */
public class NlpError extends Exception {

    public NlpError(String string) {
        super(string);
    }

    private static final long serialVersionUID = 4861728108205297716L;

}
