package clustering;

import algorithms.Clusterer;
import data.DataSet;


import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class RandomClusterer extends Clusterer{
    private static final Random RAND = new Random();

    private final int maxIterations;
    private final int updateInterval;

    private final AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }
@Override
public boolean tocontinue() {
        return tocontinue.get();
        }

public int getNumberOfClusters() { return numberOfClusters; }

public RandomClusterer(DataSet dataset,
        int maxIterations,
        int updateInterval,
        int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
        }
@Override
public void run() {
        for (int i = 1; i <= maxIterations; i++) {
        Iterator iterate = dataset.getLocations().entrySet().iterator();
        while (iterate.hasNext()) {
        Map.Entry instance = (Map.Entry)iterate.next();
        Integer labelNumber = (RAND.nextInt(getNumberOfClusters()) + 1);
        dataset.updateLabel((String) instance.getKey(), labelNumber.toString());
        }

        if (i % updateInterval == 0) {

        }
        if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
        break;
        }
        }
    }
}
