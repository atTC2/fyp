package xyz.tomclarke.fyp.nlp.preprocessing;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import xyz.tomclarke.fyp.nlp.paper.Paper;

/**
 * A class to run the Stanford CoreNLP parser of data to allow for more
 * intuitive NLP later.
 * 
 * @author tbc452
 *
 */
public class PreProcessor {

    private static final Logger log = LogManager.getLogger(PreProcessor.class);

    private StanfordCoreNLP pipeline;

    public PreProcessor() {
        // Create the CoreNLP pipeline for processing
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);
    }

    /**
     * Run the Stanford NLP annotator on the given paper
     * 
     * @param paper
     *            the Paper to annotate
     */
    public void annotate(Paper paper) {
        log.info("Processing " + paper.getLocation());
        Annotation document = new Annotation(paper.getText());
        pipeline.annotate(document);
        paper.setAnnotations(document.get(SentencesAnnotation.class));
        log.info("Finsihed processing " + paper.getLocation());
    }

}
