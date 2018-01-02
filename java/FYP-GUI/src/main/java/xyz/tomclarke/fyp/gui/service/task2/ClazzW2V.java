package xyz.tomclarke.fyp.gui.service.task2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.tomclarke.fyp.gui.dao.NlpObjectRepository;
import xyz.tomclarke.fyp.gui.dao.Paper;
import xyz.tomclarke.fyp.gui.service.NlpProcessor;

/**
 * Processes papers and classifies key phrases
 * 
 * @author tbc452
 *
 */
@Component
public class ClazzW2V implements NlpProcessor {

    @Autowired
    private NlpObjectRepository nlpObjectRepo;

    @Override
    public void loadObjects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void processPaper(xyz.tomclarke.fyp.gui.dao.Paper paper) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unload() {
        // TODO Auto-generated method stub

    }

}
