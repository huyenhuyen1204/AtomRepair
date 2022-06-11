package main;

import AST.stm.abst.StatementNode;
import AST.stm.token.MethodCalledNode;
import AST.stm.token.MethodInvocationStmNode;
import AST.stm.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindingAPI {

    public static HashMap<Integer, List<StatementNode>> parseFile(StatementNode statementNode, String parentType, String itType) {
        HashMap<Integer, List<StatementNode>> methodCalleds = new HashMap<>();
        boolean isAdd = false;
        Integer key = null;
        StatementNode tmp = statementNode;
        if (statementNode instanceof MethodCalledNode) {
            if (statementNode.getType() != null && itType != null) {
                //same type
                if (statementNode.getType().equals(itType)) {
                    if (statementNode.getParent() == null && parentType == null) { //parent == null
                        isAdd = true;
                        key = statementNode.getType().hashCode();
                    } else { //parent != null
                        if (statementNode.getParent().getType() != null
                                && parentType != null) {
                            //same Type parent
                            if (statementNode.getParent().getType()
                                    .equals(parentType)) {
                                key = (statementNode.getParent().getType() +
                                        statementNode.getType()).hashCode();
                                isAdd = true;
                            }
                        }
                    }
                }
            } else {
                //itType == null
                if (itType == null && statementNode.getType() == null) {
                    if (parentType == null && statementNode.getParent() == null) {
                        isAdd = true;
                        key = "null".hashCode();
                    } else {
                        if (parentType != null && statementNode.getParent() != null) {
                            if (statementNode.getParent().getType() != null) {
                                if (statementNode.getParent().getType()
                                        .equals(parentType)) {
                                    key = (parentType + "null").hashCode();
                                    isAdd = true;
                                }
                            }
                        }
                    }
                }
            }
            for (StatementNode param : ((MethodCalledNode) statementNode).getArguments()) {
                HashMap<Integer, List<StatementNode>> statementNodes = parseFile(param, parentType, itType);
                methodCalleds.putAll(statementNodes);
            }
        }
        //TODO
        else if (statementNode instanceof MethodInvocationStmNode) {
            //Dont care parent
            if (((MethodInvocationStmNode) statementNode).getMethodType() != null) {
                if (((MethodInvocationStmNode) statementNode).getMethodType()
                        .equals(itType)) {
                    isAdd = true;
                    // equals(x)
                    if (((MethodInvocationStmNode) statementNode).getNodes().size() == 1) {
                        tmp = statementNode.getChildren().get(0);
                        key = tmp.getType().hashCode();
                    } else {
                        // a.equals(x)
                        key = ((MethodInvocationStmNode) statementNode).getMethodType().hashCode();
                    }
                }
            }
        }
        else {
            //type = type
            if (statementNode.getType() != null) {
                if (statementNode.getType().equals(itType)) {
                    isAdd = true;
                    key = statementNode.getType().hashCode();
                }
            }
        }
        if (isAdd) {
            addToMap(methodCalleds, tmp, key);
        }

        //TODO
        if (!(statementNode instanceof main.core.token.Token)) {
            for (StatementNode child : statementNode.getChildren()) {
                if (!(child instanceof main.core.token.Token)) {
                    HashMap<Integer, List<StatementNode>> statementNodes = parseFile(child, parentType, itType);
                    for (Integer k : statementNodes.keySet()) {
                        if (methodCalleds.containsKey(k)) {
                            addStmNode(methodCalleds.get(k), statementNodes.get(k));
                        } else {
                            methodCalleds.put(k, statementNodes.get(k));
                        }
                    }
                }
            }
        }
        return methodCalleds;
    }

    public static void addStmNode(List<StatementNode> inMap, List<StatementNode> statementNodes) {
        for (StatementNode statementNode : statementNodes) {
            boolean isAdd = true;
            for (StatementNode inmap : inMap) {
                if (inmap instanceof Token && statementNode instanceof Token) {
                    if (((Token) inmap).getHashCode() == ((Token) statementNode).getHashCode()) {
                        isAdd = false;
                    }
                }
            }
            if (isAdd) {
                inMap.add(statementNode);
            }
        }
    }

    private static void addToMap(HashMap<Integer, List<StatementNode>> methodCalleds,
                                 StatementNode statementNode, Integer key) {
        if (methodCalleds.get(key) != null) {
            //exist in map
            boolean isAdd = true;
            for (StatementNode stm : methodCalleds.get(key)) {
                if (stm instanceof Token) {
                    if (((Token) stm).getHashCode() == ((Token) statementNode).getHashCode()) {
                        isAdd = false;
                        break;
                    }
                }
            }
            if (isAdd) {
                methodCalleds.get(key).add(statementNode);
            }
        } else {
            // !exist in map
            List<StatementNode> statementNodes = new ArrayList<>();
            statementNodes.add(statementNode);
            methodCalleds.put(key, statementNodes);
        }
    }
}
