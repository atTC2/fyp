package xyz.tomclarke.fyp.nlp.preprocessing;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.tomclarke.fyp.nlp.paper.Paper;

/**
 * A class which annotates all supplied data.
 * 
 * @author tbc452
 *
 */
public class PreProcessDocuments {

    private static final Logger log = LogManager.getLogger(PreProcessDocuments.class);

    public static void main(String[] args) {
        List<Paper> papers = LoadPapers.loadNewPapers();
        PreProcessor pp = new PreProcessor();
        for (Paper paper : papers) {
            if (paper.getCoreNLPAnnotations() == null) {
                pp.annotate(paper);
            } else {
                log.debug("ALREADY PROCESSED...");
            }
            paper.printAnnotations();
        }
    }

}
