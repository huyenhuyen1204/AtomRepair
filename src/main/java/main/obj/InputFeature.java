package main.obj;

import AST.stm.abst.StatementNode;
import AST.stm.token.ClassInstanceCreationNode;
import AST.stm.token.MethodCalledNode;
import main.core.token.Token;

import java.util.*;
import java.util.stream.Collectors;

public class InputFeature {
    public static final String isSameMethod = "findSameMethod";
    public static final String bugNode = "bugNode";
    public static final String fixNode = "fixNode";
    public static final String bugNode_fixNode = "bugNode_fixNode";
    public static final String bugInstance = "bugInstance";
    public static final String scope = "scope";
    public static final String context = "context";
    public static final String context_1 = "context_1";
//    public static final String fixInstance = "fixInstance";

    public HashMap<String, String> nodeMap(Boolean isSameMethod, StatementNode bugNode, StatementNode fixNode,
                                           Token.Scope scp) {
        HashMap<String, String> inputParameter = new HashMap<>();
        try {

            String sameMethod = String.valueOf(isSameMethod);
            if (isSameMethod == null) {
                sameMethod = "null";
            }
            inputParameter.put(InputFeature.bugNode, bugNode.nodeType.toString());
            inputParameter.put(InputFeature.bugNode_fixNode, bugNode.nodeType.toString() + "_" + fixNode.nodeType.toString());
            inputParameter.put(InputFeature.context_1,
                    getFixType(bugNode, fixNode, isSameMethod));
            inputParameter.put(InputFeature.context,
                    getFixType(bugNode, fixNode, isSameMethod)
                            + "&" + bugNode.getNodeInstance().toString());
//            inputParameter.put(InputFeature.context, getFixType(bugNode, fixNode, isSameMethod) + "&");
//            inputParameter.put(InputFeature.bugInstance, bugNode.getNodeInstance().toString());
//            inputParameter.put(InputFeature.scope,
//                    scp.toString());
        } catch (Exception e) {
//            if (e.getMessage().equals("EQUALS")) {
//                int index = -1;
//                int count = 0;
//                if (bugNode instanceof MethodCalledNode && fixNode instanceof MethodCalledNode) {
//                    for (int i = 0; i < ((MethodCalledNode) bugNode).getAgurements().size(); i++) {
//                        if (!((MethodCalledNode) bugNode).getAgurements().get(i).toString()
//                                .equals(((MethodCalledNode) fixNode).getAgurements().get(i).toString())) {
//                            count++;
//                            index = i;
//                        }
//                    }
//                    if (count == 1) {
//                        return nodeMap(null, ((MethodCalledNode) bugNode).getAgurements().get(index),
//                                ((MethodCalledNode) fixNode).getAgurements().get(index), Token.Scope.ALL_AFTER);
//                    } else {
//                        inputParameter.put(InputFeature.bugNode_fixNode, getFixTypeVer2(bugNode, fixNode, isSameMethod));
//                        inputParameter.put(InputFeature.bugInstance, bugNode.getNodeInstance().toString());
//                        inputParameter.put(InputFeature.scope,
//                                scp.toString());
//                    }
//                }
//            }
        }

        return inputParameter;
    }

    private String getFixType(StatementNode stmBug, StatementNode stmFix, Boolean isSameMethod) throws Exception {
        String fixType = "";
        int paramSize1 = 0;
        int paramSize2 = 0;
        List<StatementNode> param1List = null;
        List<StatementNode> param2List = null;
        String methodBug = stmBug.toString().replace(" ", "");
        String methodFix = stmFix.toString().replace(" ", "");
        fixType = stmBug.nodeType.toString() + "_" + stmFix.nodeType.toString();
        if ((stmBug instanceof MethodCalledNode && stmFix instanceof MethodCalledNode)
                || (stmBug instanceof ClassInstanceCreationNode && stmFix instanceof ClassInstanceCreationNode)) {
            if (stmBug instanceof MethodCalledNode) {
                paramSize1 = ((MethodCalledNode) stmBug).getArguments().size();
                paramSize2 = ((MethodCalledNode) stmFix).getArguments().size();
                param1List = ((MethodCalledNode) stmBug).getArguments();
                param2List = ((MethodCalledNode) stmFix).getArguments();
            } else {
                paramSize1 = ((ClassInstanceCreationNode) stmBug).getArgs().size();
                paramSize2 = ((ClassInstanceCreationNode) stmFix).getArgs().size();
                param1List = ((ClassInstanceCreationNode) stmBug).getArgs();
                param2List = ((ClassInstanceCreationNode) stmFix).getArgs();
                methodBug = methodBug.contains(".") ? methodBug
                        .substring(methodBug.lastIndexOf(".") + 1) : methodBug;
                methodFix = methodFix.contains(".") ? methodFix
                        .substring(methodFix.lastIndexOf(".") + 1) : methodFix;

                methodBug = methodBug.replaceFirst("new", "");
                methodFix = methodFix.replaceFirst("new", "");
            }


            int firstIndex = methodBug.indexOf("(");
            String method1 = methodBug.substring(0, firstIndex);
            String param1 = methodBug.substring(firstIndex);

            int firstIndexFix = methodFix.indexOf("(");
            String method2 = methodFix.substring(0, firstIndexFix);
            String param2 = methodFix.substring(firstIndexFix);

            if (method1.equals(method2)) {
                fixType += "&SameMethod";
            } else {
                fixType += "&ChangeMethod";
            }
            if (param1.equals(param2)) {
                fixType += "&SameParam";
            } else {
                fixType += "&ChangeParam";
                fixType += getAction(param1List, param2List);
//                fixType += "&ChangeParam" + action + Math.abs(paramSize1 - paramSize2);

            }
        }
        fixType +=
                (stmFix.findInClass != null ? "&" + stmFix.findInClass : "") +
                (stmFix.isSameMethod != null ? "&" + stmFix.isSameMethod : "");

        return fixType;
    }


//    private String getFixTypeVer2(StatementNode stmBug, StatementNode stmFix, Boolean isSameMethod) {
//        String fixType = "";
//        int paramSize1 = 0;
//        int paramSize2 = 0;
//        List<StatementNode> param1List = null;
//        List<StatementNode> param2List = null;
//        String methodBug = stmBug.toString().replace(" ", "");
//        String methodFix = stmFix.toString().replace(" ", "");
//
//        if ((stmBug instanceof MethodCalledNode && stmFix instanceof MethodCalledNode)
//                || (stmBug instanceof ClassInstanceCreationNode && stmFix instanceof ClassInstanceCreationNode)) {
//            if (stmBug instanceof MethodCalledNode) {
//                paramSize1 = ((MethodCalledNode) stmBug).getAgurements().size();
//                paramSize2 = ((MethodCalledNode) stmFix).getAgurements().size();
//                param1List = ((MethodCalledNode) stmBug).getAgurements();
//                param2List = ((MethodCalledNode) stmFix).getAgurements();
//
//            } else {
//                paramSize1 = ((ClassInstanceCreationNode) stmBug).getArgs().size();
//                paramSize2 = ((ClassInstanceCreationNode) stmFix).getArgs().size();
//                param1List = ((ClassInstanceCreationNode) stmBug).getArgs();
//                param2List = ((ClassInstanceCreationNode) stmFix).getArgs();
//                methodBug = methodBug.contains(".") ? methodBug
//                        .substring(methodBug.lastIndexOf(".") + 1) : methodBug;
//                methodFix = methodFix.contains(".") ? methodFix
//                        .substring(methodFix.lastIndexOf(".") + 1) : methodFix;
//
//                methodBug = methodBug.replaceFirst("new", "");
//                methodFix = methodFix.replaceFirst("new", "");
//            }
//
//
//            int firstIndex = methodBug.indexOf("(");
//            String method1 = methodBug.substring(0, firstIndex);
//            String param1 = methodBug.substring(firstIndex);
//
//            int firstIndexFix = methodFix.indexOf("(");
//            String method2 = methodFix.substring(0, firstIndexFix);
//            String param2 = methodFix.substring(firstIndexFix);
//            fixType = NodeType.MethodCalledNode.toString();
//
//            if (method1.equals(method2)) {
//                fixType += "&SameMethod";
//            } else {
//                fixType += "&ChangeMethod";
//            }
//            if (param1.equals(param2)) {
//                fixType += "&SameParam";
//            } else {
//                String action = actionVer2(param1List, param2List);
//                fixType += "&ChangeParam" + action + Math.abs(paramSize1 - paramSize2);
//            }
//        } else {
//            fixType = stmBug.nodeType.toString() + "_" + stmFix.nodeType.toString() +
//                    (isSameMethod != null ? "&" + isSameMethod.toString().toLowerCase() : "");
//        }
//        return fixType;
//    }


//    public String action(List<StatementNode> param1, List<StatementNode> param2) throws Exception {
//        String action = "";
//        if (param2.size() > param1.size()) {
//            if (constain(param2, param1)) {
//                action = "&ADD";
//            } else {
//                action = "&CHANGE";
//            }
//        } else if (param1.size() > param2.size()) {
//            if (constain(param1, param2)) {
//                action = "&REMOVE";
//            } else {
//                action = "&CHANGE";
//            }
//        } else {
//            // TH dac biet
//            List<String> candis = param1.stream().map(StatementNode::toString).collect(Collectors.toList());
//            for (int i = 0; i < param1.size(); i++) {
//                if (candis.contains(param2.get(i).toString())) {
//                    candis.remove(param2.get(i).toString());
//                }
//            }
//            if (candis.size() < 2) {
//                int index = candis.size() == 0 ? 1 : candis.size();
//                action = "&ADD" + index; // sameAdd
//            } else {
//                action = "&CHANGE"; // sameAdd
//            }
//        }
//        return action;
//    }

//    public String actionVer2(List<StatementNode> param1, List<StatementNode> param2) {
//        String action = "";
//        if (param2.size() > param1.size()) {
//            if (constain(param2, param1)) {
//                action = "&ADD";
//            } else {
//                action = "&CHANGE";
//            }
//        } else if (param1.size() > param2.size()) {
//            if (constain(param1, param2)) {
//                action = "&REMOVE";
//            } else {
//                action = "&CHANGE";
//            }
//        } else {
//            action = "&CHANGE";
//        }
//        return action;
//    }

    public boolean constain(List<StatementNode> param, List<StatementNode> paramSmaller) {
        List<String> parSmaller = paramSmaller.stream().map(StatementNode::toString)
                .collect(Collectors.toList());
        List<String> pr = param.stream().map(StatementNode::toString)
                .collect(Collectors.toList());
        int same = 0;
        for (String prsml : parSmaller) {
            if (pr.contains(prsml)) {
                same++;
            }
        }
        if (same == paramSmaller.size()) {
            return true;
        }
        return false;
    }

    public String getAction(List<StatementNode> param1, List<StatementNode> param2) {
        List<String> list1 = param1.stream().map(StatementNode::toString).collect(Collectors.toList());
        List<String> list2 = param2.stream().map(StatementNode::toString).collect(Collectors.toList());

        int min = Math.min(list2.size(), list1.size());
        int max = Math.max(list2.size(), list1.size());
        List<String> maxList = list1.size() > list2.size() ? list1 : list2;
        List<String> minList = list1.size() > list2.size() ? list2 : list1;
        int same = 0;
        for (int i = 0; i < min; i++) {
            for (int j = 0; j < max; j++) {
                if (min > i) {
                    if (minList.get(i).equals(maxList.get(j))) {
                        same++;
                        i++;
                    }
                } else {
                    break;
                }
            }
        }
        String action = "";
//
//        if (same != min) {
//            action =  "&CHANGE" + (max - same);
//        }
        if (param2.size() > param1.size()) {
            if (min == same) {
                action = "&ADD" + (max - same);
            } else {
                action = "&CHANGE";
            }
        } else if (param1.size() > param2.size()) {
            if (min == same) {
                action = "&REMOVE" + (max - same);
            } else {
                action = "&CHANGE";
            }
        } else {
            action = "&CHANGE";
        }
        if (same == 0) {
            action = "&CHANGE";
        }
        return action;
    }
}