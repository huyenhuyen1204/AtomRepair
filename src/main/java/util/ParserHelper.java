package util;

import AST.node.ClassNode;
import AST.node.FolderNode;
import AST.node.MethodNode;

import java.util.List;

public class ParserHelper {

//    public static MethodNode findMethodInClass (List<ClassNode> classNodes, String classname, String methodname, List params ) {
//        for (ClassNode classNode : classNodes) {
//            if(classNode.getName().equals(classname)) {
//                return classNode.findMethodNode(methodname, params);
//            }
//        }
//        return null;
//    }
    public static ClassNode findByClassName (FolderNode folderNode, String classname) {
        List<ClassNode> classNodes = FolderNode.getClassNodes(folderNode);
        for (ClassNode classNode : classNodes) {
            if(classNode.getName().equals(classname)) {
                return classNode;
            }
        }
        return null;
    }

//    public static List<DebugPoint> genListByMethodNode (MethodNode methodNode) {
//        List statements = methodNode.getStatements();
////        ReturnStatementParser returnStatementParser = new ReturnStatementParser(methodNode);
//
//        return null;
//    }

//    public static MethodInvocationStm getVarClassAndMethod(MethodInvocation methodInvocation) {
//        if (methodInvocation.getExpression() instanceof MethodInvocation) {
//            return getVarClassAndMethod((MethodInvocation) methodInvocation.getExpression());
//        } else {
//            if (methodInvocation.getExpression() instanceof SimpleName) {
//                String classname = ((SimpleName) methodInvocation.getExpression()).getIdentifier();
//                String methodName = methodInvocation.getName().getIdentifier();
//                List<String> argTypes =
//                return new MethodInvocationStm(classname, methodName);
//            } else {
//                return null;
//            }
//        }
//
//    }



}
