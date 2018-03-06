package xyz.tomclarke.fyp.nlp.cluster;

import java.util.ArrayList;

import xyz.tomclarke.fyp.nlp.util.NlpError;

/**
 * A representation of a cluster
 * 
 * @author tbc452
 * @param <T>
 *            The type representing tokens
 *
 */
public abstract class Cluster<T> extends ArrayList<T> {

    private static final long serialVersionUID = 9216468547163103225L;

    /**
     * Combines this and another cluster
     * 
     * @param otherCluster
     *            The cluster to combine this cluster with
     * @return The combined cluster
     */
    public abstract Cluster<T> combine(Cluster<T> otherCluster);

    /**
     * Finds the distance from this cluster to another
     * 
     * @param cluster
     *            The cluster to compare this cluster to
     * @param method
     *            The type of method for determining the distance
     * @return The distance
     * @throws NlpError
     *             If no valid method has been chosen
     */
    public double getDistance(Cluster<T> cluster, Linkage method) throws NlpError {
        switch (method) {
        case SINGLE:
            return getShortestDistance(cluster);
        case AVERAGE:
            return getAverageDistance(cluster);
        case COMPLETE:
            return getLargestDistance(cluster);
        default:
            throw new NlpError("No cluster linkage method selected.");
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
    protected abstract double getShortestDistance(Cluster<T> cluster);

    /**
     * Finds the average distance from words in this cluster to the given cluster
     * (average-linkage clustering)
     * 
     * @param cluster
     *            The cluster to compare this cluster to
     * @return The average distance
     */
    protected abstract double getAverageDistance(Cluster<T> cluster);

    /**
     * Finds the largest distance from this cluster to another (complete-linkage)
     * 
     * @param cluster
     *            The cluster to compare this cluster to
     * @return The largest distance
     */
    protected abstract double getLargestDistance(Cluster<T> cluster);

}
