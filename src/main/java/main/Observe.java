package main;

import AST.stm.abst.StatementNode;
import AST.stm.token.*;

import java.util.List;

public class Observe {

//    public static TokenExist findNodeInClass(StatementNode fixed, MethodNode methodNode, ClassNode classNode) {
////        boolean isSame = false
//        StatementNode stm = null;
//        //in methodNode
//        for (StatementNode statementNode : methodNode.getStatementNodes()) {
//            stm = findNodeInStatement(statementNode, fixed);
//            if (stm != null) {
//                return new TokenExist(statementNode.getLine(), true, stm);
//            }
//        }
//        //other method
//        for (MethodNode method : classNode.getMethodList()) {
//            if (method.hashCode() != methodNode.hashCode()) {
//                for (StatementNode statementNode : method.getStatementNodes()) {
//                    stm = findNodeInStatement(statementNode, fixed);
//                    if (stm != null) {
//                        return new TokenExist(stm.getLine(), false, stm);
//                    }
//                }
//            }
//        }
//        return new TokenExist(-1, false, null);
//    }

    private static StatementNode findNodeInStatement(StatementNode statementNode, StatementNode fixed) {
        if (statementNode != null) {
            if (statementNode.getClass().toString().equals(fixed.getClass().toString())) {
                if (statementNode instanceof ClassInstanceCreationNode) {
                    ClassInstanceCreationNode source = (ClassInstanceCreationNode) statementNode;
                    ClassInstanceCreationNode fixNode = (ClassInstanceCreationNode) fixed;
                    if (((ClassInstanceCreationNode) statementNode).getName()
                            .equals(((ClassInstanceCreationNode) fixed).getName())) {
                        //compare param
                        if (source.getArgs().size() == fixNode.getArgs().size()) {
                            if (isParam(source.getArgs(), fixNode.getArgs())) {
//                                for (int i = 0; i < fixNode.getArgs().size(); i++) {
//                                    compareStatement(source.getArgs().get(i), fixNode.getArgs().get(i));
//                                }
                                return statementNode;
                            }
                        }
                    }
                } else if (statementNode instanceof MethodCalledNode) {
                    MethodCalledNode source = (MethodCalledNode) statementNode;
                    MethodCalledNode fixNode = (MethodCalledNode) fixed;
                    StatementNode parentCandidate =  source.getParent();
                    StatementNode parenttarget = fixNode.getParent();
                    boolean isEquals = true;
                    if (parenttarget != null && parentCandidate != null) {
                        if (parenttarget.getType() == null && parentCandidate.getType() == null) {
                            isEquals = true;
                        }
                        if (parenttarget.getType() != null && parentCandidate.getType() != null) {
                            isEquals = parentCandidate.getType().equals(parenttarget.getType());
                        }
                    } else if (parenttarget == null && parentCandidate != null) {
                        isEquals = false;
                    } else if (parenttarget != null && parentCandidate == null) {
                        isEquals = false;
                    }
                    if (isEquals) {
                        if (source.getMethodName().equals(fixNode.getMethodName())) {
                            //compare param
                            if (source.getArguments().size() == fixNode.getArguments().size()) {
                                if (isParam(source.getArguments(), fixNode.getArguments())) {
//                                    for (int i = 0; i < fixNode.getAgurements().size(); i++) {
//                                        compareStatement(source.getAgurements().get(i), fixNode.getAgurements().get(i));
//                                    }
                                    return statementNode;
                                }
                            }
                        }
                    }
                } else if (statementNode instanceof MethodInvocationStmNode) {
                    if (statementNode.getChildren().size() == fixed.getChildren().size()) {
                        for (int i = 0; i < statementNode.getChildren().size(); i++) {
                            boolean isEq = compareMethodInvocation(statementNode.getChildren().get(i), fixed.getChildren().get(i));
                            if (isEq) {
                                return statementNode;
                            }
                        }
                    }
//                    boolean isSame = compareMethodInvocation(statementNode, fixed);
//                    if (isSame) {
//                        return statementNode;
//                    }
                } else if (statementNode instanceof QualifiedNameNode) {
//                    QualifiedNameNode fix = (QualifiedNameNode) fixed;
//                    QualifiedNameNode find = (QualifiedNameNode) statementNode;
//                    if (fix.getQualifier().getType().equals(find.getQualifier().getType())) {
//                        if (fix.getName().getKeyVar().equals(find.getName().getKeyVar())) {
//                            return statementNode;
//                        }
//                    }
                } else if (statementNode instanceof BooleanNode) {
                    BooleanNode stmFind = (BooleanNode) statementNode;
                    BooleanNode stmFix = (BooleanNode) fixed;
                    if (stmFind.isValue() == stmFix.isValue()) {
                        return stmFind;
                    }
                }
            }
        }
        //get child (except QualifierName)
        if (statementNode.getChildren() != null) {
            if (statementNode.getChildren().size() > 0) {
                for (StatementNode child : statementNode.getChildren()) {
                    StatementNode token = findNodeInStatement(child, fixed);
                    if (token != null) {
                        return token;
                    }
                }
            }
        }
        if (statementNode instanceof MethodCalledNode) {
            if (((MethodCalledNode) statementNode).getArguments().size() > 0) {
                for (StatementNode child : ((MethodCalledNode) statementNode).getArguments()) {
                    StatementNode token = findNodeInStatement(child, fixed);
                    if (token != null) {
                        return token;
                    }
                }
            }
        }
        return null;
    }

    private static boolean compareMethodInvocation(StatementNode bug, StatementNode fixed) {
        boolean isEqual = true;
        if ((bug instanceof BaseVariableNode) && (fixed instanceof BaseVariableNode)) {
            if (((BaseVariableNode) bug).getType() != null && ((BaseVariableNode) fixed).getType() != null) {
                if (((BaseVariableNode) bug).getType() != ((BaseVariableNode) fixed).getType()) {
                    isEqual = false;
                    return isEqual;
                }
            }
        } else if (bug instanceof MethodCalledNode && fixed instanceof MethodCalledNode) {
            if (((MethodCalledNode) bug).getHashCode() != ((MethodCalledNode) fixed).getHashCode()) {
                isEqual = false;
                return isEqual;
            }
        } else {
            return false;
        }
        if (isEqual) {
            if (bug.getChildren() != null && fixed.getChildren() != null) {
                if (bug.getChildren().size() != fixed.getChildren().size()) {
                    isEqual = false;
                    return isEqual;
                } else {
                    for (int i = 0; i < bug.getChildren().size(); i++) {
                        boolean isEq = compareMethodInvocation(bug.getChildren().get(i), fixed.getChildren().get(i));
                        if (!isEq) {
                            return false;
                        }
                    }
                }
            }
        }
        return isEqual;
    }

    private static boolean isParam(List<StatementNode> argSource, List<StatementNode> argFix) {
        boolean isParams = true;
        for (int i = 0; i < argSource.size(); i++) {
            StatementNode paramSource = argSource.get(i);
            StatementNode paramFixed = argFix.get(i);
            if (!(paramFixed instanceof Token) && !(paramSource instanceof Token)) {
                System.out.println(paramFixed.toString() + "=" + paramSource.toString());
            } else if (!(paramFixed instanceof Token) && (paramSource instanceof Token)) {
                isParams = false;
                break;
            } else if ((paramFixed instanceof Token) && !(paramSource instanceof Token)) {
                isParams = false;
                break;
            } else {
                if ((paramSource).getType() != null && (paramFixed).getType() != null)
                    if (!(paramSource).getType().equals(( paramFixed).getType())) {
                        isParams = false;
                        break;
                    }
            }
        }
        return isParams;
    }


}
