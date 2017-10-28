package xyz.tomclarke.fyp.nlp.preprocessing;

import java.util.List;

import xyz.tomclarke.fyp.nlp.paper.Paper;

/**
 * A class which annotates all supplied data.
 * 
 * @author tbc452
 *
 */
public class PreProcessDocuments {

    public static void main(String[] args) {
        List<Paper> papers = LoadPapers.loadNewPapers();
        PreProcessor pp = new PreProcessor();
        for (Paper paper : papers) {
            pp.annotate(paper);
            paper.printAnnotations();
        }
    }

}
