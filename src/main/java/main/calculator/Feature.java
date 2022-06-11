package main.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Feature {
    List<String> parameter;

    public Feature(String[] raw) {
        parameter = new ArrayList<>();
        for (String cell: raw) {
            parameter.add(cell);
        }
    }
}
