package xyz.tomclarke.fyp.nlp.evaluation;

import java.util.List;

import org.junit.Test;

import xyz.tomclarke.fyp.nlp.cluster.TestW2VCluster;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Gets some information about papers
 * 
 * @author tbc452
 *
 */
public class TestPaperAnalysis {

    @Test
    public void printPaperAnalysis() {
        List<Paper> papers = NlpUtil.loadAndAnnotateTestPapers(TestW2VCluster.class);

        for (Paper paper : papers) {
            PaperAnalysis analysis = new PaperAnalysis(paper);
            analysis.calculate();
            System.out.println(analysis);
        }
    }

}
