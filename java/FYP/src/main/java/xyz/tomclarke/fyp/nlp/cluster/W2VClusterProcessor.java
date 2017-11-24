package xyz.tomclarke.fyp.nlp.cluster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.word2vec.Word2Vec;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.util.NlpUtil;
import xyz.tomclarke.fyp.nlp.util.Tuple;

/**
 * Clusters to find key words using Word2Vec
 * 
 * @author tbc452
 *
 */
public class W2VClusterProcessor {

    private static final Logger log = LogManager.getLogger(W2VClusterProcessor.class);

    private final Word2Vec vec;
    private final List<Paper> trainingPapers;
    private Map<String, Map<String, Double>> distanceMap;

    public W2VClusterProcessor(Word2Vec vec, List<Paper> trainingPapers) {
        this.vec = vec;
        this.trainingPapers = trainingPapers;
    }

    public Word2Vec getVec() {
        return vec;
    }

    public List<Paper> getTrainingPapers() {
        return trainingPapers;
    }

    /**
     * Remove common words that shouldn't be key phrases (as they are so common they
     * don't matter that much).
     * 
     * @param originalTokens
     *            The token to process
     * @param sourcePaper
     *            The paper the tokens came from
     * @return A list of tokens to cluster on
     */
    public List<String> removeCommonTokens(List<CoreMap> originalTokens, Paper sourcePaper) {
        List<Tuple<String, Double>> tfIdfs = new ArrayList<Tuple<String, Double>>();
        for (CoreMap sentence : originalTokens) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class).toLowerCase();
                // Is it worth looking at and if we've not already seen this token, calculate a
                // TF-IDF
                if (!NlpUtil.isTokenToIgnore(word)) {
                    boolean calculatedBefore = false;
                    for (Tuple<String, Double> tfIdf : tfIdfs) {
                        if (tfIdf.getKey().equalsIgnoreCase(word)) {
                            calculatedBefore = true;
                            break;
                        }
                    }

                    if (!calculatedBefore) {
                        // Add a new TF-IDF
                        tfIdfs.add(new Tuple<String, Double>(word,
                                NlpUtil.calculateTfIdf(word, sourcePaper, trainingPapers)));
                    }
                }
            }
        }

        // Sort
        tfIdfs.sort(new Comparator<Tuple<String, Double>>() {
            @Override
            public int compare(Tuple<String, Double> p1, Tuple<String, Double> p2) {
                double diff = p1.getValue() - p2.getValue();
                if (diff > 0.0) {
                    return 1;
                } else if (diff < 0.0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        // Remove the least useful words and return
        List<String> tokens = new ArrayList<String>();
        int amountToRemove = (int) Math.round(tfIdfs.size() * 0.15);
        for (int i = amountToRemove - 1; i < tfIdfs.size(); i++) {
            tokens.add(tfIdfs.get(i).getKey());
        }

        log.debug("Selected tokens: " + tokens);
        return tokens;
    }

    /**
     * A representation of a cluster
     * 
     * @author tbc452
     *
     */
    private class Cluster extends ArrayList<String> {

        /**
         * Finds the distance from this cluster to another
         * 
         * @param cluster
         *            The cluster to compare this cluster to
         * @param method
         *            The type of method for determining the distance
         * @return The distance
         * @throws Exception
         *             If no valid method has been chosen
         */
        public double getDistance(Cluster cluster, Linkage method) throws Exception {
            switch (method) {
            case SINGLE:
                return getShortestDistance(cluster);
            case AVERAGE:
                getAverageDistance(cluster);
            case COMPLETE:
                getLargestDistance(cluster);
            default:
                throw new Exception("No cluster linkage method selected.");
            }
        }

        /**
         * Finds the shortest distance from words in this cluster to the given cluster
         * (single-linkage)
         * 
         * @param cluster
         *            The cluster to compare this cluster to
         * @return The smallest distance
         */
        private double getShortestDistance(Cluster cluster) {
            return 0;
        }

        /**
         * Finds the average distance from words in this cluster to the given cluster
         * (average-linkage clustering)
         * 
         * @param cluster
         *            The cluster to compare this cluster to
         * @return The average distance
         */
        private double getAverageDistance(Cluster cluster) {
            return 0;
        }

        /**
         * Finds the largest distance from this cluster to another (complete-linkage)
         * 
         * @param cluster
         *            The cluster to compare this cluster to
         * @return The largest distance
         */
        private double getLargestDistance(Cluster cluster) {
            return 0;
        }

    }

}
