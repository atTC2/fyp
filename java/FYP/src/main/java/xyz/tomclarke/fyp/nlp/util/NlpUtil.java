package xyz.tomclarke.fyp.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.preprocessing.LoadPapers;
import xyz.tomclarke.fyp.nlp.preprocessing.PreProcessor;

/**
 * A collection of useful utilities
 * 
 * @author tbc452
 *
 */
public abstract class NlpUtil {

    private static final Logger log = LogManager.getLogger(NlpUtil.class);
    private static List<String> ignoreList;

    /**
     * Decides if it is a token to ignore
     * 
     * @param token
     *            The token to check
     * @return If the token should be ignored
     */
    public static boolean isTokenToIgnore(String token) {
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
            try (BufferedReader br = new BufferedReader(
                    new FileReader(NlpUtil.class.getClassLoader().getResource("ignore.txt").getFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    ignoreList.add(line.toLowerCase());
                }
            } catch (IOException e) {
                log.error("Could not read token ignore file", e);
            }
        }

        return ignoreList.contains(token);
    }

    /**
     * Loads papers and annotates them
     * 
     * @param clazz
     *            The class to load them from (if it's a test/ class, it'll load
     *            from test/resources).
     * @return Annotated papers
     */
    public static <T> List<Paper> loadAndAnnotatePapers(Class<T> clazz) {
        return annotatePapers(
                LoadPapers.loadNewPapers(new File(clazz.getClassLoader().getResource("papers.txt").getFile())));
    }

    /**
     * Loads test papers and annotates them
     * 
     * @param clazz
     *            The class to load them from (if it's a test/ class, it'll load
     *            from test/resources).
     * @return Annotated test papers
     */
    public static <T> List<Paper> loadAndAnnotateTestPapers(Class<T> clazz) {
        return annotatePapers(
                LoadPapers.loadNewPapers(new File(clazz.getClassLoader().getResource("papers_test.txt").getFile())));
    }

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
            if (paper.getAnnotations() == null) {
                if (pp == null) {
                    pp = new PreProcessor();
                }
                pp.annotate(paper);
            }
        }

        return papers;
    }

    /**
     * Calculates the TF-IDF of a word in a paper
     * 
     * @param word
     *            The word to calculate the TF-IDF of
     * @param containingPaper
     *            The paper the word is from
     * @param trainingPapers
     *            The training papers
     * @return The calculates TF-IDF score of the word
     */
    public static double calculateTfIdf(String word, Paper containingPaper, List<Paper> trainingPapers) {
        double tf = (double) containingPaper.getTokenCounts().get(word.toLowerCase())
                / (double) containingPaper.getTokenCounts().values().stream().reduce(0, Integer::sum);
        int numOfPapersWithTerm = 0;
        for (Paper paperIdf : trainingPapers) {
            if (paperIdf.getTokenCounts().containsKey(word)) {
                numOfPapersWithTerm++;
            }
        }
        // Ensure this paper is counted
        if (!trainingPapers.contains(containingPaper)) {
            numOfPapersWithTerm++;
        }

        double documentCount = trainingPapers.size();
        if (!trainingPapers.contains(containingPaper)) {
            documentCount++;
        }

        double idf = Math.log(documentCount / (double) numOfPapersWithTerm);
        return tf * idf;
    }

}
