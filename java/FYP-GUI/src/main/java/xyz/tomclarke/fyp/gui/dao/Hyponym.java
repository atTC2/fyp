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
public class Hyponym {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "kp1")
    private KeyPhrase kp1;
    @ManyToOne(optional = false)
    @JoinColumn(name = "kp2")
    private KeyPhrase kp2;

    @Override
    public String toString() {
        return "R" + id + "\tHyponym-of Arg1:" + kp1.getId() + " Arg2:" + kp2.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KeyPhrase getKp1() {
        return kp1;
    }

    public void setKp1(KeyPhrase kp1) {
        this.kp1 = kp1;
    }

    public KeyPhrase getKp2() {
        return kp2;
    }

    public void setKp2(KeyPhrase kp2) {
        this.kp2 = kp2;
    }

}
