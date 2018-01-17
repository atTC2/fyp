package xyz.tomclarke.fyp.gui.model;

/**
 * A row of a key phrase table for the key phrase view
 * 
 * @author tbc452
 *
 */
public class KPViewRow {

    private String keyPhrase;
    private String paper;
    private long paperId;

    public KPViewRow(String keyPhrase, String paper, long paperId) {
        super();
        this.keyPhrase = keyPhrase;
        this.paper = paper;
        this.paperId = paperId;
    }

    public String getKeyPhrase() {
        return keyPhrase;
    }

    public void setKeyPhrase(String keyPhrase) {
        this.keyPhrase = keyPhrase;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }

}
