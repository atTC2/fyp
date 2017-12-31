package xyz.tomclarke.fyp.gui.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Hyponym {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(optional = false)
    private KeyPhrase kp1;
    @ManyToOne(optional = false)
    private KeyPhrase kp2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
