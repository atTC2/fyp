package xyz.tomclarke.fyp.gui.model;

/**
 * Represents a line of search result
 * 
 * @author tbc452
 *
 */
public class SearchResult {

    private Long id;
    private String paper;
    private Long kps;
    private Long rels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public Long getKps() {
        return kps;
    }

    public void setKps(Long kps) {
        this.kps = kps;
    }

    public Long getRels() {
        return rels;
    }

    public void setRels(Long rels) {
        this.rels = rels;
    }

}
