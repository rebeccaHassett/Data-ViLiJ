package algorithmSet;

import ui.ClassificationConfig;

public class ClassificationAlgorithm extends Algorithm{
    public ClassificationConfig config;
    public ClassificationAlgorithm(String algorithmType, String algorithmName, ClassificationConfig config) {
        super(algorithmType, algorithmName);
        this.config = config;
    }
}
