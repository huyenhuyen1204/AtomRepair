package main.core.token;

import AST.stm.abst.StatementNode;
import AST.stm.node.TypeNode;
import AST.stm.token.ClassInstanceCreationNode;
import AST.stm.token.MethodCalledNode;
import AST.stm.token.MethodInvocationStmNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodToken extends Token {
    private String methodName;
    private List<Token> paramTokens = new ArrayList<>();

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }

    private boolean isConstructor = false;

    public List<Token> getArgTokens() {
        return paramTokens;
    }

    public void setParamTokens(List<Token> paramTokens) {
        this.paramTokens = paramTokens;
    }


    public MethodToken(MethodCalledNode methodCalledNode, int deepLevel) {
        super();
        this.methodName = methodCalledNode.getMethodName();
        this.deepLevel = deepLevel;
        for (StatementNode statementNode : methodCalledNode.getArguments()) {
            TypeToken argToken = new TypeToken(this.deepLevel + 1);
            if (statementNode instanceof AST.stm.token.Token || statementNode instanceof TypeNode) {
                if (statementNode instanceof MethodInvocationStmNode) {
                    argToken.setNodeType(((MethodInvocationStmNode) statementNode).getMethodType());
                } else {
                    argToken.setNodeType(statementNode.getType());
                }
            } else {
                argToken.setCandidates(Collections.singletonList(statementNode));
            }
            this.paramTokens.add(argToken);
        }
    }

    public MethodToken(ClassInstanceCreationNode classInstanceCreationNode, int deepLevel) {
        super();
        this.methodName = classInstanceCreationNode.getName() ;
        this.deepLevel = deepLevel;
        this.isConstructor = true;
        for (StatementNode statementNode : classInstanceCreationNode.getArgs()) {
            TypeToken argToken = new TypeToken(this.deepLevel + 1);
            if (statementNode instanceof MethodInvocationStmNode) {
                argToken.setNodeType(((MethodInvocationStmNode) statementNode).getMethodType());
            } else {
                argToken.setNodeType(statementNode.getType());
            }
            this.paramTokens.add(argToken);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(methodName).append("(");
        for (int i = 0; i < paramTokens.size(); i++) {
            Token param = paramTokens.get(i);
            builder.append(param.toString());
            if (i < paramTokens.size() - 1) builder.append(",");
        }
        builder.append(")");
        return builder.toString();
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

}
