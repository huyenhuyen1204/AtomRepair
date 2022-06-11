package util;//package util;
//
//import core.object.BreakPointHit;
//import core.object.DebugPoint;
//
//import java.util.List;
//
//public class DebuggerHelper {
//    public static DebugPoint findDebugPoint(BreakPointHit breakPointHit, List<DebugPoint> debugPoints) {
//        for (DebugPoint debugPoint : debugPoints) {
//            if (debugPoint.getClassname().equals(breakPointHit.getClassName())) {
//                if (debugPoint.getLine() == breakPointHit.getLine()) {
//                    return debugPoint;
//                }
//            }
//        }
//        return null;
//    }
//
//    public static BreakPointHit parserLogRunning(String logRunning) {
//        String[] logs = logRunning.split(" ");
//        String[] classAndMethod = logs[0].split("\\.");
//        //get methodName
//        String methodname = classAndMethod[classAndMethod.length - 1].replace("(),", "");
//        String classname = "";
//        for (int i = 0; i < classAndMethod.length - 1; i++) {
//            classname += classAndMethod[i] + ".";
//        }
//        if (!classname.equals("")) {
//            // get classname
//            classname = classname.substring(0, classname.length() - 1);
//            String lineString = logs[logs.length - 2];
//            int line = Integer.parseInt(lineString.replace("line=", ""));
//            return new BreakPointHit(classname, methodname, line);
//        } else {
//            return null;
//        }
//    }
//}
