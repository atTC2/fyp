package xyz.tomclarke.fyp.gui.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Represents all a user would need to input about a paper to get the ball
 * rolling
 * 
 * @author tbc452
 *
 */
public class PaperLocation {

    @NotNull
    @Size(min = 5, max = 200)
    @Pattern(regexp = "")
    private String location;

    public PaperLocation() {
        // Nothing to do here
    }

    public PaperLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}