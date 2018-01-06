package xyz.tomclarke.fyp.gui.model;

import java.util.List;

/**
 * Represents what the paper view page can show
 * 
 * @author tbc452
 *
 */
public class PaperView {

    private Long id;
    private boolean validPaper;
    private String title;
    private boolean successful;
    private boolean failure;
    private String progress;
    private String author;
    private String text;
    private List<String> kps;
    // TODO support for relationships

    public PaperView() {
        // Nothing to do here
    }

    public PaperView(Long id, boolean validPaper, String title, boolean successful, boolean failure, String progress,
            String author, String text, List<String> kps) {
        super();
        this.id = id;
        this.validPaper = validPaper;
        this.title = title;
        this.successful = successful;
        this.failure = failure;
        this.progress = progress;
        this.author = author;
        this.text = text;
        this.kps = kps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isValidPaper() {
        return validPaper;
    }

    public void setValidPaper(boolean validPaper) {
        this.validPaper = validPaper;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getKps() {
        return kps;
    }

    public void setKps(List<String> kps) {
        this.kps = kps;
    }

}
