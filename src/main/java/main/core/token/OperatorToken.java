package main.core.token;

import AST.stm.abst.StatementNode;

public class OperatorToken extends Token {

    public OperatorToken(StatementNode targetNode, Scope targetScope) {
        super();
        if (targetNode.getParent() == null) {
            this.parentType = null;
        } else {
            this.parentType = targetNode.getParent().getType();
        }
        this.target = targetNode;
        this.targetScope = targetScope;
    }
    @Override
    public String toString() {
        return "<" + this.nodeType + ">";
    }
}
