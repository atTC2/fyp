package xyz.tomclarke.fyp.gui.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Synonym {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(optional = false)
    private SynLink synLink;
    @ManyToOne(optional = false)
    private KeyPhrase kp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SynLink getSynLink() {
        return synLink;
    }

    public void setSynLink(SynLink synLink) {
        this.synLink = synLink;
    }

    public KeyPhrase getKp() {
        return kp;
    }

    public void setKp(KeyPhrase kp) {
        this.kp = kp;
    }

}
