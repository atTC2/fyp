package xyz.tomclarke.fyp.nlp.cluster;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import xyz.tomclarke.fyp.nlp.TestOnPapers;
import xyz.tomclarke.fyp.nlp.cluster.W2VClusterProcessor.W2VCluster;
import xyz.tomclarke.fyp.nlp.cluster.W2VClusterProcessor.W2VClusterSet;
import xyz.tomclarke.fyp.nlp.paper.Paper;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecPretrained;
import xyz.tomclarke.fyp.nlp.word2vec.Word2VecProcessor;

/**
 * Tests clustering using Word2Vec
 * 
 * @author tbc452
 *
 */
public class TestW2VCluster extends TestOnPapers {

    @Test
    public void testClusteringGN() throws Exception {
        testClustering(new W2VClusterProcessor(Word2VecProcessor.loadPreTrainedData(Word2VecPretrained.GOOGLE_NEWS),
                trainingPapers));
    }

    /**
     * Runs some assessments on generated clusters
     * 
     * @param cluster
     *            The cluster object to test with
     * @throws Exception
     */
    private void testClustering(W2VClusterProcessor cluster) throws Exception {
        for (Paper testPaper : testPapers) {
            analyseClustering(cluster, testPaper, Linkage.SINGLE);
            analyseClustering(cluster, testPaper, Linkage.AVERAGE);
            analyseClustering(cluster, testPaper, Linkage.COMPLETE);
            break;
        }
    }

    /**
     * Analyse clustering
     * 
     * @param cluster
     *            The cluster object to test with
     * @param paper
     *            The paper to cluster the tokens of
     * @param method
     *            The method of determining distance between each cluster
     * @throws Exception
     */
    private void analyseClustering(W2VClusterProcessor cluster, Paper paper, Linkage method) throws Exception {
        List<W2VClusterSet> clusterHistory = cluster.cluster(paper, method);
        List<List<Integer>> historyInNumbers = new ArrayList<List<Integer>>();

        for (W2VClusterSet set : clusterHistory) {
            List<Integer> historyOfSet = new ArrayList<Integer>();
            for (W2VCluster c : set) {
                historyOfSet.add(c.size());
            }
            historyInNumbers.add(historyOfSet);
        }

        for (List<Integer> historyOfSet : historyInNumbers) {
            String sizes = "";
            for (Integer h : historyOfSet) {
                sizes += h + ",";
            }
            System.out.println(sizes);
        }
    }
}
