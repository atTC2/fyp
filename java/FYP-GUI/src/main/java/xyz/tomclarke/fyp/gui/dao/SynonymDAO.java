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
@Table(name = "synonym")
public class SynonymDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "syn_link")
    private SynLinkDAO synLink;
    @ManyToOne(optional = false)
    @JoinColumn(name = "kp")
    private KeyPhraseDAO kp;

    @Override
    public String toString() {
        return "*\tSynonym-of T" + kp.getRelativeId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SynLinkDAO getSynLink() {
        return synLink;
    }

    public void setSynLink(SynLinkDAO synLink) {
        this.synLink = synLink;
    }

    public KeyPhraseDAO getKp() {
        return kp;
    }

    public void setKp(KeyPhraseDAO kp) {
        this.kp = kp;
    }

}
