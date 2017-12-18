package xyz.tomclarke.fyp.nlp.word2vec;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.Extraction;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.svm.TestKeyPhraseSVM;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * A test class to let you mess around with Google News and Word2Vec (very much
 * just testing fun)
 * 
 * @author tbc452
 *
 */
public class TestWord2Vec {

    private static final Logger log = LogManager.getLogger(TestWord2Vec.class);

    @Ignore
    @Test
    public void trySimilarity() throws Exception {
        log.info("Testing similarity - accepting cli input...");
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        while (!exit) {
            String input = scanner.nextLine();
            switch (input) {
            case "exit":
                exit = true;
                break;
            default:
                String clazzInput = scanner.nextLine();
                log.info("Processing: " + input + " + " + clazzInput);
                log.info(vec.similarity(input, clazzInput));
            }
        }
        log.info("Finished testing similarity");
        scanner.close();
    }

    @Ignore
    @Test
    public void calculateKeyPhraseValues() {
        List<Paper> papers = NlpUtil.loadAndAnnotatePapers(TestKeyPhraseSVM.class);
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        for (Paper paper : papers) {
            for (Extraction ext : paper.getExtractions()) {
                if (!(ext instanceof KeyPhrase)) {
                    // Not a key phrase
                    continue;
                }
                KeyPhrase kp = (KeyPhrase) ext;

                String[] tokens = kp.getPhrase().split(" ");

                for (String token : tokens) {
                    double[] values = vec.getWordVector(token);
                    log.info(token + ": " + values.length + " values...");
                    printVec(values);
                }
            }
            break;
        }
    }

    @Ignore
    @Test
    public void calculateKeyPhraseCombinedValues() {
        List<Paper> papers = NlpUtil.loadAndAnnotatePapers(TestKeyPhraseSVM.class);
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        for (Paper paper : papers) {
            for (Extraction ext : paper.getExtractions()) {
                if (!(ext instanceof KeyPhrase)) {
                    // Not a key phrase
                    continue;
                }
                KeyPhrase kp = (KeyPhrase) ext;

                // Remove punctuation and get each word
                String[] tokens = kp.getPhrase().replaceAll("\\p{Punct}", "").split(" ");

                // Turn into list for Word2Vec, ignoring words that are not in Word2Vec
                List<String> positiveWords = new ArrayList<String>();
                for (String token : tokens) {
                    if (vec.hasWord(token)) {
                        positiveWords.add(token);
                    }
                }

                // Get the nearest word
                String nearestWord = new ArrayList<String>(vec.wordsNearest(positiveWords, new ArrayList<String>(), 1))
                        .get(0);

                log.info(kp.getPhrase() + " -> " + nearestWord);
            }
            // Could take a while
            break;
        }
    }

    @Ignore
    @Test
    public void calculateKeyPhraseVector() {
        List<Paper> papers = NlpUtil.loadAndAnnotatePapers(TestKeyPhraseSVM.class);
        Word2Vec vec = Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
        double largest = 0.0;
        double smallest = 0.0;
        for (Paper paper : papers) {
            for (Extraction ext : paper.getExtractions()) {
                if (!(ext instanceof KeyPhrase)) {
                    // Not a key phrase
                    continue;
                }
                KeyPhrase kp = (KeyPhrase) ext;

                // Remove punctuation and get each word
                String[] tokens = kp.getPhrase().replaceAll("\\p{Punct}", "").split(" ");

                int expectedVectorLength = 300;
                double[] vector = new double[expectedVectorLength];

                // Sum token vectors, ignoring words that are not in Word2Vec
                for (String token : tokens) {
                    if (vec.hasWord(token)) {
                        double[] wordVector = vec.getWordVector(token);
                        Assert.assertEquals(expectedVectorLength, wordVector.length);
                        for (int i = 0; i < wordVector.length; i++) {
                            vector[i] += wordVector[i];
                        }
                    }
                }

                for (double d : vector) {
                    if (d > largest) {
                        largest = d;
                    }
                    if (d < smallest) {
                        smallest = d;
                    }
                }

                // log.info(kp.getPhrase() + ":");
                // printVec(vector);

            }
            log.info("Processed paper");
        }
        log.info("Smallest: " + String.valueOf(smallest) + " Largest: " + String.valueOf(largest));
    }

    @Test
    public void testLoadGoogleNews() throws Exception {
        Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS);
    }

    @Test
    public void testLoadWiki2Vec() throws Exception {
        Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.WIKI2VEC);
    }

    @Test
    public void testLoadFreebaseIDs() throws Exception {
        Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.FREEBASE_IDS);
    }

    @Test
    public void testLoadFreebaseNames() throws Exception {
        Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.FREEBASE_NAMES);
    }

    /**
     * Prints out a Word Vector from W2V
     * 
     * @param values
     *            The values to print
     */
    private void printVec(double[] values) {
        String valuesString = "";
        for (double val : values) {
            valuesString += String.valueOf(val) + ", ";
        }
        log.info(valuesString);
    }

}
