package main.core.pattern;

import AST.stm.abst.StatementNode;

import java.util.HashMap;
import java.util.Map;

public class MethodPattern extends Pattern {

    private StatementNode changedMethod = null; // not null if there is a changed method

    protected Map<StatementNode, Pattern> changedArgsMap = null; // not null if changedMethod not null

    public Map<StatementNode, Pattern> getChangedArgsMap() {
        return changedArgsMap;
    }

    public MethodPattern() {
        super();
        changedArgsMap = new HashMap<>();
    }

    public StatementNode getChangedMethod() {
        return changedMethod;
    }

    public void setChangedMethod(StatementNode changedMethod) {
        this.changedMethod = changedMethod;
    }

    public void setChangedArgsMap(Map<StatementNode, Pattern> changedArgsMap) {
        this.changedArgsMap = changedArgsMap;
        this.tokenChangedSize = changedArgsMap.size();
    }
}
