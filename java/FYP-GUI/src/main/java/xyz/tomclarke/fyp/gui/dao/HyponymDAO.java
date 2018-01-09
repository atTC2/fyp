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
@Table(name = "hyponym")
public class HyponymDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "relative_id")
    private Long relativeId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "kp1")
    private KeyPhraseDAO kp1;
    @ManyToOne(optional = false)
    @JoinColumn(name = "kp2")
    private KeyPhraseDAO kp2;

    @Override
    public String toString() {
        return "R" + relativeId + "\tHyponym-of Arg1:T" + kp1.getRelativeId() + " Arg2:T" + kp2.getRelativeId();
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

    public KeyPhraseDAO getKp1() {
        return kp1;
    }

    public void setKp1(KeyPhraseDAO kp1) {
        this.kp1 = kp1;
    }

    public KeyPhraseDAO getKp2() {
        return kp2;
    }

    public void setKp2(KeyPhraseDAO kp2) {
        this.kp2 = kp2;
    }

}
