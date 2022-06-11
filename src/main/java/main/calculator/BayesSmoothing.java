package main.calculator;

import config.Configuration;
import main.obj.InputFeature;
import util.FileHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BayesSmoothing {
    public HashMap<String, HashMap<String, Integer>> statisticOfNodes = new HashMap<>();
    public HashMap<String, Integer> nodeCountTotal;


    public BayesSmoothing() {
        if (statisticOfNodes.size() == 0) {
            List<Feature> nodes = FileHelper.readObservationCSV(Configuration.nodePath);
            CountResult nodeCount = statistic(nodes);
            statisticOfNodes = nodeCount.statistic;
            nodeCountTotal = nodeCount.total;
        }
    }

    public float calculatorScore(HashMap<String, String> values) {
        //statistic Bayes smoothing P = (n + k)/(total + v*k)
        // - v: number of unique string in raw
        // - n: count of unique strings
        // k = 1
        int k = 1;
        float score = 0;

        for (String key : values.keySet()) {
            String value = values.get(key);
            int n = 0;
            int total = 0;
            int v = 0;
            boolean sameMethodIsNull = false;
            if (statisticOfNodes.containsKey(key)) {
                if (statisticOfNodes.get(key).containsKey(value)) {
                    n = statisticOfNodes.get(key).get(value);
                } else if (key.equals(InputFeature.isSameMethod)
                        && value.equals("NULL")) { // if isSameMethod = null -> p = 0.5
                    sameMethodIsNull = true;
                }
            }
            total = nodeCountTotal.get(key);
            v = statisticOfNodes.get(key).size();
            float paramScore;
            if (sameMethodIsNull) {
                paramScore = 0.5f;
            } else {
                paramScore = (float) (n + k) / (total + v * k);
            }
            score = score == 0 ? 1 : score;
            score = score * paramScore;
        }
        return score;
    }

    public float caculatorStatistical (HashMap<String, String> values) {
        int bug_fixCount = -1;
        int bugContextCount = -1;
        int bugInstanceTotal = -1;
        int context1 = -1;
        for (String key : values.keySet()) {
            String value = values.get(key);
            if (key.equals(InputFeature.context)) {
                bugContextCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.bugNode)){
                bugInstanceTotal =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.bugNode_fixNode)) {
                bug_fixCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.context_1)) {
                context1 = statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            }
        }
//        float bayes1 = (float) (bug_fixCount+1)/(bugInstanceTotal+1);
        float bayes2 = (float) (bugContextCount+1)/(bugInstanceTotal+1);
        float bayes1 = (float) (context1+1)/(bugInstanceTotal+1);
        return (float) (bayes1+bayes2)/2;
    }


    /**
     * HOW???
     * @param values
     * @return
     */
    public float caculatorStatisticalMAXVer2 (HashMap<String, String> values) {
        int bug_fixCount = -1;
        int bugContextCount = -1;
        int bugInstanceTotal = -1;

        for (String key : values.keySet()) {
            String value = values.get(key);
            if (key.equals(InputFeature.context)) {
                bugContextCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            }
        }
        float bayes2 = ((float)(bugContextCount+1))/((float)(nodeCountTotal.get(InputFeature.context)+1));
        return bayes2;
    }

    /**
     * THESIS ok ver1 27/29 bugs
     * @param values
     * @return
     */
    public float caculatorStatisticalMAXVer1(HashMap<String, String> values) {
        int bug_fixCount = -1;
        int bugContextCount = -1;
        int bugInstanceTotal = -1;

        for (String key : values.keySet()) {
            String value = values.get(key);
            if (key.equals(InputFeature.context)) {
                bugContextCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.bugNode)){
                bugInstanceTotal =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            }
//            else if (key.equals(InputFeature.bugNode_fixNode)) {
//                bug_fixCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
//            }
        }
//        float bayes1 = (float) (bug_fixCount+1)/(bugInstanceTotal+1);
        float bayes2 = (float) (bugContextCount+1)/(bugInstanceTotal+1);
        return bayes2;
    }

    public float caculatorStatistical2 (HashMap<String, String> values) {
        int bug_fixCount = -1;
        int bugContextCount = -1;
        int bugInstanceTotal = -1;

        for (String key : values.keySet()) {
            String value = values.get(key);
            if (key.equals(InputFeature.context)) {
                bugContextCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.bugNode)){
                bugInstanceTotal =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            }
//            else if (key.equals(InputFeature.bugNode_fixNode)) {
//                bug_fixCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
//            }
        }
//        float bayes1 = (float) (bug_fixCount+1)/(bugInstanceTotal+1);
        float bayes2 = (float) (bugContextCount+1)/(bugInstanceTotal+1);
        return bayes2;
    }

    public float caculatorStatistical3 (HashMap<String, String> values) {
        int bug_fixCount = -1;
        int bugContextCount = -1;
        int context1 = -1;
        int bugInstanceTotal = -1;

        for (String key : values.keySet()) {
            String value = values.get(key);
            if (key.equals(InputFeature.context)) {
                bugContextCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.bugNode)){
                bugInstanceTotal =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.bugNode_fixNode)) {
                bug_fixCount =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            } else if (key.equals(InputFeature.context_1)) {
                context1 =  statisticOfNodes.get(key).get(value) == null ? 0 : statisticOfNodes.get(key).get(value);
            }
        }
        float bayes1 = (float) (context1+1)/(bugInstanceTotal+1);
        float bayes2 = (float) (bugContextCount+1)/(bugInstanceTotal+1);
        return (float) (bayes2 + bayes1)/2;
    }

    private CountResult statistic(List<Feature> features) {
        HashMap<String, Integer> total = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> table = new HashMap<>();
        List<String> title = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            List<String> cells = features.get(i).parameter;
            if (i == 0) {
                for (int j = 0; j < cells.size(); j++) {
                    table.put(cells.get(j), new HashMap<>());
                    title.add(cells.get(j));
                    total.put(cells.get(j), 0);
                }
            } else {
                for (int j = 0; j < cells.size(); j++) {
                    if (!table.get(title.get(j)).containsKey(cells.get(j))) {
                        table.get(title.get(j)).put(cells.get(j), 1);
                    } else {
                        Integer old = table.get(title.get(j)).get(cells.get(j));
                        //update
                        table.get(title.get(j)).put(cells.get(j), old + 1);
                    }
                    if (!cells.get(j).equals("null")) {
                        List<String> keys = new ArrayList(total.keySet());
                        total.put(keys.get(j), total.get(keys.get(j)) + 1);
                    }
                }
            }
        }

//        int k = 1;
//        for (int i = 0; i < title.size(); i ++) {
//            for (String key : table.get(title.get(i)).keySet()) {
//                Float old = table.get(title.get(i)).get(key);
//                Float bayesSmoothing = (old + k) / (total.get(i) + table.get(title.get(i)).size());
//                //update
//                table.get(title.get(i)).put(key, bayesSmoothing);
//            }
//        }
        return new CountResult(table, total);
    }

    public static void main(String[] args) {
        BayesSmoothing bayesSmoothing = new BayesSmoothing();
        System.out.println(bayesSmoothing.statisticOfNodes.size());
    }
}
