package xyz.tomclarke.fyp.gui.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import xyz.tomclarke.fyp.gui.dao.PaperDAO;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Offers utilities for handling papers
 * 
 * @author tbc452
 *
 */
@Service
public class PaperUtil {

    private static final Logger log = LogManager.getLogger(PaperUtil.class);
    private List<Paper> trainingPapers;
    private Map<Paper, Map<String, Integer>> trainingPapersTokenCounts;

    /**
     * Loads training papers
     * 
     * @throws IOException
     */
    @PostConstruct
    public void initialise() throws IOException {
        log.info("Loading PaperUtil");
        // Load all training papers
        trainingPapers = NlpUtil.loadAndAnnotatePapers(true);

        // Generate data for TF-IDF calculations (different to nlp module as based off
        // my token generator rather than CoreNLP tokens)
        trainingPapersTokenCounts = new HashMap<Paper, Map<String, Integer>>();
        for (Paper paper : trainingPapers) {
            Map<String, Integer> tokenCounts = getPaperTokenCounts(paper);
            trainingPapersTokenCounts.put(paper, tokenCounts);
        }
        log.info("Loaded PaperUtil");
    }

    /**
     * Gets the training papers
     * 
     * @return Training papers
     */
    public List<Paper> getTrainingPapers() {
        return trainingPapers;
    }

    /**
     * Converts a loaded Paper object to the actual object
     * 
     * @param paperFromDb
     *            The object to convert back
     * @return The Paper object loaded from the database
     */
    public Paper loadPaper(PaperDAO paperFromDb) {
        if (paperFromDb != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(paperFromDb.getParse());
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (Paper) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error("Could not load Paper object from database, ID " + paperFromDb.getId(), e);
                // It broke, so try making the object again as if it wasn't found
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Converts a paper object to bytes for saving in the database
     * 
     * @param paper
     *            The paper to convert
     * @return The bytes
     * @throws IOException
     */
    public byte[] getPaperBytes(Paper paper) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(paper);
        return baos.toByteArray();
    }

    /**
     * Calculates the TF-IDF of a word in a paper
     * 
     * @param originalWord
     *            The word to calculate the TF-IDF of
     * @param containingPaper
     *            The paper the word is from
     * @return The calculates TF-IDF score of the word
     */
    public double calculateTfIdf(String originalWord, Paper containingPaper) {
        // Prepare for calculations
        String token = NlpUtil.sanitiseString(originalWord);

        Map<String, Integer> tokenCounts = getPaperTokenCounts(containingPaper);
        if (!tokenCounts.containsKey(token)) {
            // Try the actual word
            token = originalWord.toLowerCase();
            if (!tokenCounts.containsKey(token)) {
                // Can't handle this as its split differently
                log.debug(
                        "TF-IDF calculator can't find \"" + originalWord + "\" in paper " + containingPaper.getTitle());

                // Can't find it, hm...
                // If stop word, go 0, if not stop word, go 1
                return NlpUtil.isTokenToIgnore(originalWord) ? 0.0 : 1.0;
            }
        }

        double tf = (double) tokenCounts.get(token) / (double) tokenCounts.values().stream().reduce(0, Integer::sum);
        int numOfPapersWithTerm = 0;
        for (Map<String, Integer> trainingPaperTokens : trainingPapersTokenCounts.values()) {
            if (trainingPaperTokens.containsKey(token)) {
                numOfPapersWithTerm++;
            }
        }
        // Add one for this paper
        numOfPapersWithTerm++;

        // All the documents + this one
        double documentCount = trainingPapers.size() + 1;

        double idf = Math.log(documentCount / (double) numOfPapersWithTerm);
        return tf * idf;
    }

    /**
     * Gets the token counts of papers
     * 
     * @param paper
     *            The paper to get token counts of
     * @return The token counts
     */
    private Map<String, Integer> getPaperTokenCounts(Paper paper) {
        Map<String, Integer> tokenCounts = new HashMap<String, Integer>();
        for (String token : NlpUtil.getAllTokens(paper.getText())) {
            if (!tokenCounts.containsKey(token)) {
                // Not already seen, add
                tokenCounts.put(token, 1);
            } else {
                // Already seen, add
                tokenCounts.put(token, tokenCounts.get(token) + 1);
            }
        }
        return tokenCounts;
    }

}
