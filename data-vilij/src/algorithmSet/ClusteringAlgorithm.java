package algorithmSet;

import ui.ClusteringConfig;

public class ClusteringAlgorithm extends Algorithm{
    public ClusteringConfig config;
    public ClusteringAlgorithm(String algorithmType, String algorithmName, ClusteringConfig config) {
        super(algorithmType, algorithmName);
        this.config = config;
    }
}
