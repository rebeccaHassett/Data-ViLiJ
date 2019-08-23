package algorithms;

import algorithms.Algorithm;
import data.DataSet;

/**
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm {

    protected final int numberOfClusters;

    public int getNumberOfClusters() { return numberOfClusters; }

    public Clusterer(int k) {
        if (k < 1)
            k = 1;
        else if (k > 4)
            k = 4;
        numberOfClusters = k;
    }
    protected DataSet dataset;
    public DataSet getDataSet() {
        return this.dataset;
    }
}