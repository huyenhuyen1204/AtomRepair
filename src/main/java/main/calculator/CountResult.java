package main.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CountResult {
    public List<CountResult> generatedInstances = new ArrayList<>();
    public HashMap<String, HashMap<String, Integer>> statistic;
    public HashMap<String, Integer> total;

    public CountResult(HashMap<String, HashMap<String, Integer>> statistic, HashMap<String, Integer> total) {
        this.statistic = statistic;
        this.total = total;
        generatedInstances.add(this);
    }
}
