package xyz.tomclarke.fyp.gui.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Synonym {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private KeyPhrase kp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KeyPhrase getKp() {
        return kp;
    }

    public void setKp(KeyPhrase kp) {
        this.kp = kp;
    }

}
