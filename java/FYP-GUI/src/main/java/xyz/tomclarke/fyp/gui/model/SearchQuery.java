package xyz.tomclarke.fyp.gui.model;

import javax.validation.constraints.Size;

/**
 * Represents a search query
 * 
 * @author tbc452
 *
 */
public class SearchQuery {

    @Size(min = 0, max = 30)
    private String task;
    @Size(min = 0, max = 30)
    private String process;
    @Size(min = 0, max = 30)
    private String material;

    public SearchQuery() {
        // Nothing to do here
    }

    public SearchQuery(String task, String process, String material) {
        this.task = task;
        this.process = process;
        this.material = material;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

}
