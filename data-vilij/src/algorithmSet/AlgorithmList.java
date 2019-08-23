package algorithmSet;

import java.util.HashMap;

public class AlgorithmList {
    private HashMap<String, Algorithm> algoList = new HashMap<>();
    public void addAlgorithmToList(String name, Algorithm algorithm) {
        algoList.put(name, algorithm);
    }
    public Algorithm getAlgorithmFromList(String name) {
       return algoList.get(name);
    }
}
