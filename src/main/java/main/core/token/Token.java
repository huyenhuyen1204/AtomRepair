package main.core.token;

import AST.stm.abst.StatementNode;

import java.util.ArrayList;
import java.util.List;

public class Token extends StatementNode {
    public List<Token> generatedTokens = new ArrayList<>();

    protected Scope targetScope; // ALL_AFTER or ONLY_CURRENT
    protected String nodeType;
    protected StatementNode target;

    protected String originalValue = null;

    protected String parentType;
    protected List<StatementNode> candidates;

    public Token() {
        super();
        this.candidates = new ArrayList<>();
        generatedTokens.add(this);
    }

    public StatementNode getTarget() {
        return target;
    }

    public void setTarget(StatementNode target) {
        this.target = target;
    }
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Scope getTargetScope() {
        return targetScope;
    }

    public void setTargetScope(Scope targetScope) {
        this.targetScope = targetScope;
    }

    public List<StatementNode> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<StatementNode> candidates) {
        this.candidates = new ArrayList<>(candidates);
    }

    public enum Scope {
        ALL_AFTER, ONLY_CURRENT
    }
}
