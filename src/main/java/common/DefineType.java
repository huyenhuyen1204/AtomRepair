package common;

import java.util.HashMap;

public class DefineType {
    public static HashMap<String, String> types;
    static {
        types = new HashMap<>();
        types.put("length", "int");
        types.put("size()", "int");
    }
//    public static boolean isList (String objType) {
//        return objType.contains("<") && objType.contains(">")
//                || objType.contains("[") && objType.contains("]");
//    }
}
