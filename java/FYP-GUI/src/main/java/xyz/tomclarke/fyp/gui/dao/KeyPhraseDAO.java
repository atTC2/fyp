package xyz.tomclarke.fyp.gui.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "key_phrase")
public class KeyPhraseDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "relative_id")
    private Long relativeId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "paper")
    private PaperDAO paper;
    @Column(name = "start")
    private Integer start;
    @Column(name = "end")
    private Integer end;
    @Column(name = "text")
    private String text;
    @Column(name = "classification")
    private String classification;

    @Override
    public String toString() {
        return "T" + relativeId + "\t" + classification + " " + start + " " + end + "\t" + text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(Long relativeId) {
        this.relativeId = relativeId;
    }

    public PaperDAO getPaper() {
        return paper;
    }

    public void setPaper(PaperDAO paper) {
        this.paper = paper;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

}
