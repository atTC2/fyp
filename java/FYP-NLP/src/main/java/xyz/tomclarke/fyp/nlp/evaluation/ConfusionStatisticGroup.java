package xyz.tomclarke.fyp.nlp.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.paper.extraction.KeyPhrase;

/**
 * Handles evaluation of a extraction, covering all basis
 * 
 * @author tbc452
 *
 */
public class ConfusionStatisticGroup {

    private static final Logger log = LogManager.getLogger(ConfusionStatisticGroup.class);

    private final boolean includeClazz;
    private final List<ConfusionStatistic> overallStatsGen;
    private final List<ConfusionStatistic> overallStatsInc;
    private final List<ConfusionStatistic> overallStatsStr;
    private final List<ConfusionStatistic> overallStatsRls;
    private final List<ConfusionStatistic> overallBoundary;

    public ConfusionStatisticGroup(boolean includeClazz) {
        this.includeClazz = includeClazz;
        overallStatsGen = new ArrayList<ConfusionStatistic>();
        overallStatsInc = new ArrayList<ConfusionStatistic>();
        overallStatsStr = new ArrayList<ConfusionStatistic>();
        overallStatsRls = new ArrayList<ConfusionStatistic>();
        overallBoundary = new ArrayList<ConfusionStatistic>();
    }

    /**
     * Adds a result set to the cumulative results
     * 
     * @param predictedKeyPhrases
     *            The predicted phrases
     * @param paper
     *            The paper they are from (with the gold standard embedded)
     */
    public void add(List<KeyPhrase> predictedKeyPhrases, Paper paper) {
        overallStatsGen.add(EvaluateExtractions.evaluateKeyPhrases(predictedKeyPhrases, paper, paper.getKeyPhrases(),
                Strictness.GENEROUS, includeClazz));
        overallStatsInc.add(EvaluateExtractions.evaluateKeyPhrases(predictedKeyPhrases, paper, paper.getKeyPhrases(),
                Strictness.INCLUSIVE, includeClazz));
        overallStatsStr.add(EvaluateExtractions.evaluateKeyPhrases(predictedKeyPhrases, paper, paper.getKeyPhrases(),
                Strictness.STRICT, includeClazz));
        overallStatsRls.add(EvaluateExtractions.evaluateKeyPhrases(predictedKeyPhrases, paper, paper.getKeyPhrases(),
                Strictness.REALLY_STRICT, includeClazz));
        overallBoundary.add(
                EvaluateExtractions.evaluateKeyPhrasesOnBoundaries(predictedKeyPhrases, paper.getKeyPhrases(), paper));
    }

    /**
     * Print out the statistics
     */
    public void log() {
        ConfusionStatistic gen = ConfusionStatistic.calculateScoreSum(overallStatsGen);
        ConfusionStatistic inc = ConfusionStatistic.calculateScoreSum(overallStatsInc);
        ConfusionStatistic str = ConfusionStatistic.calculateScoreSum(overallStatsStr);
        ConfusionStatistic rls = ConfusionStatistic.calculateScoreSum(overallStatsRls);
        ConfusionStatistic boundary = ConfusionStatistic.calculateScoreSum(overallBoundary);

        log.info("Overall statistics (gen): " + gen);
        log.info("Specific results were: tp: " + gen.getTp() + " fp: " + gen.getFp() + " tn: " + gen.getTn() + " fn: "
                + gen.getFn());
        log.info("Overall statistics (inc): " + inc);
        log.info("Specific results were: tp: " + inc.getTp() + " fp: " + inc.getFp() + " tn: " + inc.getTn() + " fn: "
                + inc.getFn());
        log.info("Overall statistics (str): " + str);
        log.info("Specific results were: tp: " + str.getTp() + " fp: " + str.getFp() + " tn: " + str.getTn() + " fn: "
                + str.getFn());
        log.info("Overall statistics (rls): " + rls);
        log.info("Specific results were: tp: " + rls.getTp() + " fp: " + rls.getFp() + " tn: " + rls.getTn() + " fn: "
                + rls.getFn());
        log.info("Boundary statistics: " + boundary);
        log.info("Specific results were: tp: " + boundary.getTp() + " fp: " + boundary.getFp() + " tn: "
                + boundary.getTn() + " fn: " + boundary.getFn());
    }

}
