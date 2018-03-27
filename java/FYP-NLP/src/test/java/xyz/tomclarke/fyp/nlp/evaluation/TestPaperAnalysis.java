package xyz.tomclarke.fyp.nlp.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import xyz.tomclarke.fyp.nlp.TestOnPapers;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;

/**
 * Gets some information about papers
 * 
 * @author tbc452
 *
 */
public class TestPaperAnalysis extends TestOnPapers {

    @Ignore
    @Test
    public void printKpPairsAnalysis() {
        int totalPairs = 0;
        for (Paper paper : trainingPapers) {
            int kps = paper.getKeyPhrases().size();
            totalPairs += (kps * kps) - kps;
        }
        System.out.println("Total KP pairs: " + totalPairs);
    }

    @Ignore
    @Test
    public void printPaperAnalysis() {
        for (Paper paper : trainingPapers) {
            PaperAnalysis analysis = new PaperAnalysis(paper);
            analysis.calculate();
            System.out.println(analysis);
        }
    }

    @Ignore
    @Test
    public void printTokenAnalysis() {
        Map<String, Integer> tokens = new HashMap<String, Integer>();
        for (Paper paper : trainingPapers) {
            for (String token : paper.getTokenCounts().keySet()) {
                if (!tokens.containsKey(token)) {
                    // Not already seen, add
                    tokens.put(token, paper.getTokenCounts().get(token));
                } else {
                    // Already seen, add
                    tokens.put(token, tokens.get(token) + paper.getTokenCounts().get(token));
                }
            }
        }

        int totalTokens = tokens.values().stream().reduce(0, Integer::sum);
        int diffTokens = tokens.keySet().size();

        double size = 0.0;
        for (String token : tokens.keySet()) {
            size += token.length();
        }
        size /= (double) diffTokens;

        System.out.println("There are " + totalTokens + " with " + diffTokens
                + " different tokens with the average length being " + size + ".");
    }

    @Ignore
    @Test
    public void printKPLengthAnalysis() {
        double size = 0.0;
        double count = 0.0;
        for (Paper paper : trainingPapers) {
            for (KeyPhrase kp : paper.getKeyPhrases()) {
                for (String word : NlpUtil.getAllTokens(kp.getPhrase())) {
                    size += word.length();
                    count++;
                }
            }
        }

        System.out.println("Average KP length: " + size / count);
    }

    @Ignore
    @Test
    public void printTFIDFAnalysis() throws IOException {
        Map<String, Double> tfIdfs = new HashMap<String, Double>();
        for (Paper paper : trainingPapers) {
            for (String token : paper.getTokenCounts().keySet()) {
                tfIdfs.put(token, NlpUtil.calculateTfIdf(token, paper, trainingPapers));
            }
        }

        FileWriter fileWriter = new FileWriter("tfidfs.csv");
        fileWriter.append("token|tfidf");
        fileWriter.append(System.lineSeparator());

        for (String token : tfIdfs.keySet()) {
            fileWriter.append(token);

            fileWriter.append("|");

            fileWriter.append(tfIdfs.get(token).toString());
            fileWriter.append(System.lineSeparator());
        }

        fileWriter.close();
    }

    @Ignore
    @Test
    public void isAnyPhraseAllStopTokens() {
        List<Paper> papersToCheck = new ArrayList<Paper>();
        papersToCheck.addAll(trainingPapers);
        papersToCheck.addAll(testPapers);
        for (Paper paper : papersToCheck) {
            for (KeyPhrase kp : paper.getKeyPhrases()) {
                boolean kpHasNonStopWord = false;
                for (String token : NlpUtil.getAllTokens(kp.getPhrase())) {
                    if (!NlpUtil.isTokenToIgnore(token)) {
                        kpHasNonStopWord = true;
                        break;
                    }
                }
                if (!kpHasNonStopWord) {
                    System.out
                            .println("KP made of stop words: \"" + kp.getPhrase() + "\" in paper " + paper.getTitle());
                }
            }
        }
    }

}
