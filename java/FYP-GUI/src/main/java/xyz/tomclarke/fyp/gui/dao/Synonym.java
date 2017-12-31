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
public class Synonym {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "syn_link")
    private SynLink synLink;
    @ManyToOne(optional = false)
    @JoinColumn(name = "kp")
    private KeyPhrase kp;

    @Override
    public String toString() {
        return "*\tSynonym-of " + kp.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
