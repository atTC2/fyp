package xyz.tomclarke.fyp.nlp.paper;

import java.util.List;

import xyz.tomclarke.fyp.nlp.preprocessing.PreProcessor;

/**
 * Utility functions for papers
 * 
 * @author tbc452
 *
 */
public class PaperUtil {

    /**
     * Runs the Stanford CoreNLP annotator on all papers supplied
     * 
     * @param papers
     *            papers to annotate
     * @return The annotated papers
     */
    public static List<Paper> annotatePapers(List<Paper> papers) {
        PreProcessor pp = null;
        for (Paper paper : papers) {
            if (paper.getCoreNLPAnnotations() == null) {
                if (pp == null) {
                    pp = new PreProcessor();
                }
                pp.annotate(paper);
            }
        }

        return papers;
    }

}
