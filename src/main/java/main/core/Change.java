package main.core;

import AST.stm.abst.StatementNode;
import main.core.token.Token;

import java.util.List;

public class Change {
    private StatementNode originalNode;
    private Token token;
    private int tokenKey;

    private List<Change> changes = null;

    public void setOriginalNode(StatementNode originalNode) {
        this.originalNode = originalNode;
    }

    public StatementNode getOriginalNode() {
        return originalNode;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (token.getTargetScope() == Token.Scope.ONLY_CURRENT) {
            builder.append(originalNode.toString()).append(" -> ");
            builder.append(token.toString());

        } else if (token.getTargetScope() == Token.Scope.ALL_AFTER) {
            String suffix = originalNode.getSuffix();
            suffix = suffix.equals("") ? suffix : "." + suffix;
            builder.append(originalNode.toString()).append(suffix);
            builder.append(" -> ").append(token.toString());
        }
        return builder.toString();
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public int getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(int tokenKey) {
        this.tokenKey = tokenKey;
    }
}
