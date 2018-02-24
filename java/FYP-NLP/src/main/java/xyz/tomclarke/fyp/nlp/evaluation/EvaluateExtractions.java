package xyz.tomclarke.fyp.nlp.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;
import xyz.tomclarke.fyp.nlp.paper.extraction.Relationship;

/**
 * Used to evaluate extractions
 * 
 * @author tbc452
 *
 */
public abstract class EvaluateExtractions {

    private static final Logger log = LogManager.getLogger(EvaluateExtractions.class);

    /**
     * Evaluates key phrases (for now we ignore ID and text position)
     * 
     * @param pred
     *            Predicted key phrases
     * @param paper
     *            The paper the key phrases are for
     * @param act
     *            Actual key phrases
     * @param strict
     *            Whether to match key phrases perfectly (actual.equals(predicted))
     *            or if we'll accept close matches
     * @param includeClazz
     *            Whether or not to consider classification of the key phrase
     * @return Statistics based off of a confusion matrix
     */
    public static ConfusionStatistic evaluateKeyPhrases(List<KeyPhrase> pred, Paper paper, List<KeyPhrase> act,
            Strictness strictness, boolean includeClazz) {
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;

        // Actual KPs not in this list are false negatives
        List<KeyPhrase> foundKP = new ArrayList<KeyPhrase>();

        // Check each predicted key phrase...
        for (KeyPhrase predKP : pred) {
            // ... against each actual key phrase
            boolean matched = false;
            for (KeyPhrase actKP : act) {

                boolean matchingPhrase = false;
                boolean matchingClazz = false;

                String predKPWord = predKP.getPhrase().toLowerCase();
                String actKPWord = actKP.getPhrase().toLowerCase();
                // Check key phrase
                switch (strictness) {
                case GENEROUS:
                    matchingPhrase = predKPWord.contains(actKPWord) || actKPWord.contains(predKPWord);
                    break;
                case INCLUSIVE:
                    matchingPhrase = predKPWord.contains(actKPWord);
                    // TESTING - investigating the extra bits that damage the strict F1 score
                    /*
                     * if (matchingPhrase) { try (BufferedWriter bw = new BufferedWriter( new
                     * FileWriter("/home/tom/FYP/inclusive_differences.txt", true))) {
                     * bw.write(predKPWord + "|" + actKPWord + "|" + (predKPWord.length() -
                     * actKPWord.length())); bw.newLine(); bw.close(); } catch (IOException e) {
                     * log.error("inclusive differences write error", e); } }
                     */
                    break;
                case STRICT:
                    matchingPhrase = predKPWord.equals(actKPWord);
                    break;
                case REALLY_STRICT:
                    matchingPhrase = predKPWord.equals(actKPWord) && predKP.getPosition().equals(actKP.getPosition());
                }

                // Check classification of key phrase
                if (includeClazz) {
                    matchingClazz = predKP.getClazz().equals(actKP.getClazz());
                } else {
                    // As we're not concerned with this
                    matchingClazz = true;
                }

                if (matchingPhrase && matchingClazz) {
                    // Result decided
                    tp++;
                    matched = true;
                    if (!foundKP.contains(actKP)) {
                        foundKP.add(actKP);
                    }
                    continue;
                }
            }

            if (!matched) {
                fp++;
            }
        }

        fn = act.size() - foundKP.size();

        // Get count of true negatives...
        for (CoreMap sentence : paper.getAnnotations()) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                // Don't recount positives
                boolean inPredKP = false;
                for (KeyPhrase phrase : pred) {
                    inPredKP = phrase.getPhrase().contains(word);
                    if (inPredKP) {
                        break;
                    }
                }
                // Don't count false negatives
                boolean inActKP = false;
                for (KeyPhrase phrase : pred) {
                    inActKP = phrase.getPhrase().contains(word);
                    if (inActKP) {
                        break;
                    }
                }

                // If it's not a positive and if it's not a false negative
                if (!inPredKP && !inActKP) {
                    tn++;
                }
            }
        }

        return ConfusionStatistic.calculateScore(tp, fp, tn, fn);
    }

    /**
     * Evaluates key phrases based on boundaries
     * 
     * @param pred
     *            The predicted key phrases
     * @param act
     *            The actual key phrases
     * @param paper
     *            The paper the key phrases are for
     * @return The statistics on key phrase extraction
     */
    public static ConfusionStatistic evaluateKeyPhrasesOnBoundaries(List<KeyPhrase> pred, List<KeyPhrase> act,
            Paper paper) {
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;

        for (int i = 0; i < paper.getText().length(); i++) {
            // First, is it in predicted key phrases...
            boolean inPred = isIndexInKeyPhraseList(i, pred);
            // Is it in actual?
            boolean inAct = isIndexInKeyPhraseList(i, act);

            // Increment correct scenario
            if (inPred && inAct) {
                tp++;
            } else if (inPred && !inAct) {
                fp++;
            } else if (!inPred && inAct) {
                fn++;
            } else if (!inPred && !inAct) {
                tn++;
            }
        }

        return ConfusionStatistic.calculateScore(tp, fp, tn, fn);
    }

    /**
     * Tests to see if the index is within the list of key phrases given
     * 
     * @param index
     *            The index to check
     * @param kps
     *            The key phrases to search through
     * @return
     */
    private static boolean isIndexInKeyPhraseList(int index, List<KeyPhrase> kps) {
        for (KeyPhrase kp : kps) {
            if (index >= kp.getPosition().getStart() && index < kp.getPosition().getEnd()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates statistics on relation extractions (this method requires both sets
     * to be based upon the same key phrase objects)
     * 
     * @param pred
     *            The predicted relationships
     * @param act
     *            The actual relationships
     * @param kps
     *            All possible KPs
     * @return The statistics
     */
    public static ConfusionStatistic evaluateRelationships(List<Relationship> pred, List<Relationship> act,
            List<KeyPhrase> kps) {
        double tp = 0;
        double fp = 0;
        double tn = 0;
        double fn = 0;

        List<Relationship> foundCorrectly = new ArrayList<Relationship>();

        for (Relationship predRel : pred) {
            boolean truePositive = false;
            for (Relationship actRel : act) {
                if (predRel.getType().equals(actRel.getType())) {
                    // Same type, how about content?
                    if (predRel.getPhrases().length != actRel.getPhrases().length) {
                        // Different key phrases listed
                        continue;
                    }

                    for (int i = 0; i < predRel.getPhrases().length; i++) {
                        if (predRel.getPhrases()[i].equals(actRel.getPhrases()[i])) {
                            truePositive = true;
                            if (!foundCorrectly.contains(actRel)) {
                                foundCorrectly.add(actRel);
                            } else {
                                log.error("Rel-eval: found a relation that was added twice!");
                            }
                            break;
                        }
                    }
                }
            }

            if (truePositive) {
                tp++;
            } else {
                fp++;
            }
        }

        // Missed positives
        fn = act.size() - foundCorrectly.size();

        // True negatives are all combinations not in the actual or predicted set of
        // relations...
        for (KeyPhrase kp1 : kps) {
            for (KeyPhrase kp2 : kps) {
                if (kp1 == kp2) {
                    continue;
                }

                // Check if it is in either set
                boolean inRelationship = false;
                for (Relationship predRel : pred) {
                    if (predRel.getPhrases()[0] == kp1 && predRel.getPhrases()[1] == kp2) {
                        inRelationship = true;
                        break;
                    }
                }
                if (!inRelationship) {
                    for (Relationship actRel : act) {
                        if (actRel.getPhrases()[0] == kp1 && actRel.getPhrases()[1] == kp2) {
                            inRelationship = true;
                            break;
                        }
                    }
                }

                // Not in any predicted or actual relationship, so true negative
                if (!inRelationship) {
                    tn++;
                }
            }
        }

        return ConfusionStatistic.calculateScore(tp, fp, tn, fn);
    }
}
