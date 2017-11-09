package xyz.tomclarke.fyp.nlp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;
import xyz.tomclarke.fyp.nlp.preprocessing.PreProcessor;
import xyz.tomclarke.fyp.nlp.svm.SVMProcessor;

/**
 * A class which annotates all supplied data.
 * 
 * @author tbc452
 *
 */
public class ProcessDocuments {

    private static final Logger log = LogManager.getLogger(ProcessDocuments.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        List<Paper> papers = LoadPapers.loadNewPapers(
                new File(new LoadPapers().getClass().getClassLoader().getResource("papers.txt").getFile()));
        PreProcessor pp = null;
        for (Paper paper : papers) {
            log.info(paper);
            paper.printKeyPhraseInformation();
            if (paper.getCoreNLPAnnotations() == null) {
                if (pp == null) {
                    pp = new PreProcessor();
                }
                pp.annotate(paper);
            } else {
                log.debug("Already processed...");
            }
        }

        // Processed all papers

        // Word2Vec
        // Word2Vec vec = Word2VecProcessor.process(papers);
        //
        // WordVectorSerializer.writeWord2VecModel(vec, new File("test.csv"));

        // SVM
        SVMProcessor svm = new SVMProcessor();
        svm.generateTrainingData(papers);
        try {
            svm.train();
        } catch (Exception e) {
            log.error("Error training SVM", e);
        }
    }

}
