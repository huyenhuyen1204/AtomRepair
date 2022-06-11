package main.core.token;

import AST.stm.abst.StatementNode;
import AST.stm.token.InfixExpressionStmNode;

import java.util.ArrayList;
import java.util.List;

public class TypeToken extends Token {
    List<MethodToken> methodTokens;

    public List<MethodToken> getMethodTokens() {
        return methodTokens;
    }

    public void setMethodTokens(List<MethodToken> methodTokens) {
        this.methodTokens = methodTokens;
    }

    @Override
    public String toString() {
        return "<" + this.nodeType + ">";
    }

    public TypeToken(int deepLevel) {
        super();
        this.deepLevel = deepLevel;
        this.targetScope = Scope.ALL_AFTER;
    }


    public TypeToken(StatementNode targetNode, Scope targetScope) {
        super();
        methodTokens = new ArrayList<>();
        if (targetNode.getParent() == null) {
            this.parentType = null;
        } else {
            if (!(targetNode.getParent() instanceof InfixExpressionStmNode)) {
                this.parentType = targetNode.getParent().getType();
            }
        }
        this.target = targetNode;
        this.targetScope = targetScope;
    }


}
