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
    private String text;
    private boolean focusOnTask;
    private boolean focusOnProcess;
    private boolean focusOnMaterial;

    public SearchQuery() {
        // Nothing to do here
    }

    public SearchQuery(String text, boolean focusOnTask, boolean focusOnProcess, boolean focusOnMaterial) {
        super();
        this.text = text;
        this.focusOnTask = focusOnTask;
        this.focusOnProcess = focusOnProcess;
        this.focusOnMaterial = focusOnMaterial;
    }

    @Override
    public String toString() {
        return "QUERY:\"" + text + "\", T:" + focusOnTask + ", P:" + focusOnProcess + ", M:" + focusOnMaterial;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFocusOnTask() {
        return focusOnTask;
    }

    public void setFocusOnTask(boolean focusOnTask) {
        this.focusOnTask = focusOnTask;
    }

    public boolean isFocusOnProcess() {
        return focusOnProcess;
    }

    public void setFocusOnProcess(boolean focusOnProcess) {
        this.focusOnProcess = focusOnProcess;
    }

    public boolean isFocusOnMaterial() {
        return focusOnMaterial;
    }

    public void setFocusOnMaterial(boolean focusOnMaterial) {
        this.focusOnMaterial = focusOnMaterial;
    }

}
