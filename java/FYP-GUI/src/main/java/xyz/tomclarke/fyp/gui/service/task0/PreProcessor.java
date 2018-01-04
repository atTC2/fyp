package xyz.tomclarke.fyp.gui.service.task0;

import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;
import xyz.tomclarke.fyp.nlp.annotator.Annotator;

/**
 * Fills in the paper database object and adds annotation information (an actual
 * Paper object)
 * 
 * @author tbc452
 *
 */
@Component
public class PreProcessor implements NlpProcessor {

    private Annotator ann;

    @Override
    public void loadObjects() throws Exception {
        ann = new Annotator();
    }

    @Override
    public void processPaper(PaperDAO paper) throws Exception {
        // TODO make the paper object
    }

    @Override
    public void unload() throws Exception {
        ann = null;
        System.gc();
    }

}
