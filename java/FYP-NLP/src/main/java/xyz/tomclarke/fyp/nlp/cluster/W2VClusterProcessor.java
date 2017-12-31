package xyz.tomclarke.fyp.nlp.cluster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
 * Clusters using hierarchical to find key words using Word2Vec
 * 
 * @author tbc452
 *
 */
public class W2VClusterProcessor {

    private static final Logger log = LogManager.getLogger(W2VClusterProcessor.class);

    private final Word2Vec vec;
    private final List<Paper> trainingPapers;

    public W2VClusterProcessor(Word2Vec vec, List<Paper> trainingPapers) {
        this.vec = vec;
        this.trainingPapers = trainingPapers;
    }

    /**
     * Remove common words that shouldn't be key phrases (as they are so common they
     * don't matter that much).
     * 
     * @param sourcePaper
     *            The paper containing the tokens to process
     * @return A list of tokens to cluster on
     */
    private List<String> removeCommonTokens(Paper sourcePaper) {
        List<CoreMap> originalTokens = sourcePaper.getAnnotations();
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
     * Generates the distance map from the words given
     * 
     * @param tokens
     *            The tokens to generate a distance map of
     */
    private Map<String, Map<String, Double>> generateDistanceMap(List<String> tokens) {
        Map<String, Map<String, Double>> distanceMap = new HashMap<String, Map<String, Double>>();
        for (String wordFrom : tokens) {
            Map<String, Double> distancesFromWord = new HashMap<String, Double>();
            distanceMap.put(wordFrom, distancesFromWord);
            for (String wordTo : tokens) {
                distancesFromWord.put(wordTo, vec.similarity(wordFrom, wordTo));
            }
        }

        return distanceMap;
    }

    /**
     * Cluster to find key words
     * 
     * @param paper
     *            The paper to cluster the tokens of
     * @param method
     *            The method of determining distance between each cluster
     * @return The history of hierarchical clustering
     * @throws Exception
     *             If there was a problem
     */
    public List<W2VClusterSet> cluster(Paper paper, Linkage method) throws Exception {
        // Get the tokens we care about
        List<String> tokens = removeCommonTokens(paper);
        // Generate a distance map between each pair of tokens
        Map<String, Map<String, Double>> distanceMap = generateDistanceMap(tokens);

        // To keep a history of each iteration
        List<W2VClusterSet> clusterHistory = new ArrayList<W2VClusterSet>();

        // Make the initial clusters (each word is a cluster)
        W2VClusterSet clusters = new W2VClusterSet();
        for (String token : tokens) {
            W2VCluster cluster = new W2VCluster(distanceMap);
            cluster.add(token);
            clusters.add(cluster);
        }
        // Save the first version of the cluster set
        clusterHistory.add(clusters.copy());

        // Now iterate, combining the closest clusters until there is just 1 left
        while (clusters.size() > 1) {
            double clusterDistance = Double.MAX_VALUE;
            W2VCluster clusterA = null;
            W2VCluster clusterB = null;

            for (W2VCluster clusterFrom : clusters) {
                for (W2VCluster clusterTo : clusters) {
                    // Don't try and match clusters that are the same
                    if (clusterFrom.equals(clusterTo)) {
                        continue;
                    }

                    double currentClusterDistance = clusterFrom.getDistance(clusterTo, method);
                    if (currentClusterDistance < clusterDistance) {
                        // We've found a closer cluster than currently saved, note these down
                        clusterDistance = currentClusterDistance;
                        clusterA = clusterFrom;
                        clusterB = clusterTo;
                    }
                }
            }

            // Ensure we found clusters close together
            if (clusterA == null || clusterB == null) {
                log.warn("Could not iterate on W2VCluster any further, end cluster count: " + clusters.size());
                break;
            }

            // Combine the closest clusters
            log.debug("Combining clusters with distance " + clusterDistance);
            W2VCluster combined = (W2VCluster) clusterA.combine(clusterB);
            // Remove the clusters from the current cluster set and add the combined version
            clusters.remove(clusterA);
            clusters.remove(clusterB);
            clusters.add(combined);

            // Save the new cluster set
            clusterHistory.add(clusters.copy());
        }

        log.info("Generated hierarchical cluster using " + method);
        log.debug(clusterHistory);

        return clusterHistory;
    }

    /**
     * A collection of clusters, which support copying (so they can be saved in a
     * history)
     * 
     * @author tbc452
     *
     */
    protected class W2VClusterSet extends ArrayList<W2VCluster> {

        private static final long serialVersionUID = -3215804144763193247L;

        /**
         * Generates a copy of the cluser set (so the original set can be saved)
         * 
         * @return A copy of this cluster set
         */
        public W2VClusterSet copy() {
            W2VClusterSet copy = new W2VClusterSet();
            for (W2VCluster cluster : this) {
                copy.add(cluster);
            }
            return copy;
        }

    }

    /**
     * A representation of a Word2Vec cluster
     * 
     * @author tbc452
     *
     */
    protected class W2VCluster extends Cluster<String> {

        private static final long serialVersionUID = -6694787882359474571L;
        private final Map<String, Map<String, Double>> distanceMap;

        public W2VCluster(Map<String, Map<String, Double>> distanceMap) {
            super();
            this.distanceMap = distanceMap;
        }

        @Override
        public Cluster<String> combine(Cluster<String> otherCluster) {
            W2VCluster combinedCluster = new W2VCluster(distanceMap);

            // Get tokens from this cluster
            for (String token : this) {
                combinedCluster.add(token);
            }

            // Get the tokens from the other cluster
            for (String token : otherCluster) {
                combinedCluster.add(token);
            }

            return combinedCluster;
        }

        @Override
        protected double getShortestDistance(Cluster<String> cluster) {
            double shortestDistance = Double.MAX_VALUE;

            for (String wordFrom : this) {
                for (String wordTo : cluster) {
                    if (distanceMap.get(wordFrom).get(wordTo) < shortestDistance) {
                        shortestDistance = distanceMap.get(wordFrom).get(wordTo);
                    }
                }
            }

            return shortestDistance;
        }

        @Override
        protected double getAverageDistance(Cluster<String> cluster) {
            double acc = 0.0;
            double counter = 0.0;
            for (String wordFrom : this) {
                for (String wordTo : cluster) {
                    acc += distanceMap.get(wordFrom).get(wordTo);
                    counter++;
                }
            }

            return acc / counter;
        }

        @Override
        protected double getLargestDistance(Cluster<String> cluster) {
            double largestDistance = Double.MIN_VALUE;

            for (String wordFrom : this) {
                for (String wordTo : cluster) {
                    if (distanceMap.get(wordFrom).get(wordTo) > largestDistance) {
                        largestDistance = distanceMap.get(wordFrom).get(wordTo);
                    }
                }
            }

            return largestDistance;
        }

    }

}
