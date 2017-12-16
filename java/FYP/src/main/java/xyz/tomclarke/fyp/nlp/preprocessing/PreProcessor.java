package xyz.tomclarke.fyp.nlp.preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.tomclarke.fyp.nlp.annotator.Annotator;
import xyz.tomclarke.fyp.nlp.paper.Paper;

/**
 * A class to run the Stanford CoreNLP parser of data to allow for more
 * intuitive NLP later.
 * 
 * @author tbc452
 *
 */
public class PreProcessor extends Annotator {

    private static final Logger log = LogManager.getLogger(PreProcessor.class);

    /**
     * Run the Stanford NLP annotator on the given paper
     * 
     * @param paper
     *            the Paper to annotate
     */
    public void annotate(Paper paper) {
        log.info("Processing " + paper.getLocation());
        paper.setAnnotations(annotate(paper.getText()));
        log.info("Finsihed processing " + paper.getLocation());
    }

}
