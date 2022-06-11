package main;

import AST.stm.abst.StatementNode;
import main.core.PatchCandidate;

import java.util.ArrayList;
import java.util.List;

public class TokenCandidate {
    private StatementNode targetNode;
    private List<PatchCandidate> candidates; // element
    public int level;

    public TokenCandidate(StatementNode targetNode, int level) {
        this.targetNode = targetNode;
        this.level = level;
        candidates = new ArrayList<>();
    }

    public StatementNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(StatementNode targetNode) {
        this.targetNode = targetNode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<PatchCandidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<PatchCandidate> candidates) {
        this.candidates = candidates;
    }
}
