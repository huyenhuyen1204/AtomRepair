package main.calculator;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;


public class CaculatorDistance {
    public float caculateDistance(String keyVar, String keyVar1, int id) {
        AbstractStringMetric metric3 = null;

        if (id == 5) { // 2
            metric3 = new ChapmanLengthDeviation();
        } else if (id == 6) {
            metric3 = new ChapmanMatchingSoundex();
        } else if (id == 13) {
            metric3 = new Jaro();
        } else if (id == 14) { //3
            metric3 = new JaroWinkler();
        } else if (id == 15) {
            metric3 = new Levenshtein();
        } else if (id == 18) {
            metric3 = new NeedlemanWunch();
        } else if (id == 20) {
            metric3 = new QGramsDistance();
        } else if (id == 21) {
            metric3 = new Soundex();
        } else if (id == 23) { // 1
            metric3 = new TagLinkToken();
        }

        try {
            float result3 = metric3.getSimilarity(keyVar, keyVar1);
            return result3;
        } catch (Exception e) {
            return -1;
        }
    }

    public static float caculateDistance(String keyVar2, String keyVar1) {
        String string1 = keyVar1.replace(" ", "");
        String string2 = keyVar2.replace(" ", "");

        String compare1;
        String compare2;

        String sub1 = "";
        if (keyVar1.contains("(") && keyVar1.contains(")")) {
             sub1 = keyVar1.substring(keyVar1.indexOf("("), keyVar1.lastIndexOf(")") + 1);
             string1 = keyVar1.replace(sub1, "");
        }
        String sub2 = "";
        if (keyVar2.contains("(") && keyVar2.contains(")")) {
            sub2 = keyVar2.substring(keyVar2.indexOf("("), keyVar2.lastIndexOf(")") + 1);
            string2 = keyVar2.replace(sub2, "");
        }

        //methodName != methodName
        if (!string1.equals(string2)) {
            compare1 = string1;
            compare2 = string2;
        } else {
            compare1 = sub1.replace("(", "").replace(")", "");
            compare2 = sub2.replace("(", "").replace(")", "");
        }

        AbstractStringMetric metric3 = new SmithWatermanGotoh();

        try {
            float result3 = metric3.getSimilarity(compare1, compare2);
//            float result1 = metric1.getSimilarity(string1, string2);
            return result3;
        } catch (Exception e) {
            return -1;
        }
    }


    public static void main(String[] args) {
//        List<Integer> ids = new ArrayList<>();
//        for (int i = 1; i < 24; i++) {
//            float score1 = CaculatorDistance.caculatorDistence("createEdge(fromNode,Branch.UNCOND,finallyNode)"
//                    , "Branch.UNCOND", i);
//            float score2 = CaculatorDistance.caculatorDistence("Branch.UNCOND"
//                    , "Branch.ON_EX", i);
//            if (score2 > score1) {
//
//                System.out.println("caculatorDistence" + i + ": " + score1 + "-" + score2 + "==>" + (score2 - score1));
//            }
//        }
        System.out.println(CaculatorDistance.caculateDistance("RegularTimePeriod.DEFAULT_TIME_ZONE", "zone"));
//        System.out.println(CaculatorDistance.caculatorDistence("parent", "fromNode"));
//        System.out.println("ID MAXXXX: " + idMax);

    }
}
