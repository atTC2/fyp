package xyz.tomclarke.fyp.gui.model;

/**
 * Represents a key phrase cloud entity
 * 
 * @author tbc452
 *
 */
public class KPViewCloudEntity {

    private String text;
    private double weight;

    public KPViewCloudEntity(String text, double weight) {
        super();
        this.text = text;
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
