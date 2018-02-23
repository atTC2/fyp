package xyz.tomclarke.fyp.nlp.paper;

/**
 * Holds sudo paper information for convenience
 * 
 * @author tbc452
 *
 */
public class PseudoPaper extends Paper {

    private static final long serialVersionUID = 8249906152080715746L;

    public PseudoPaper(String text) {
        super("MEMORY", false, false);
        setText(text);
    }

}
