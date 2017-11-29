package xyz.tomclarke.fyp.nlp.paper.extraction;

import java.io.Serializable;

/**
 * Represents the position of a keyword.
 * 
 * @author tbc452
 *
 */
public class Position implements Serializable {

    private static final long serialVersionUID = -2531234398793842145L;
    private int start;
    private int end;

    public Position(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
