package AST.stm.nodetype;

public class InitNode {
    private int level;
    private int line;
    private String varname;
    private String type;
    public InitNode() {
        super();
    }

    public InitNode(int level, String varname,  String type, int line) {
        this.level = level;
        this.varname = varname;
        this.line = line;
        this.type = type;
//        this.statementsUsed = new ArrayList();
    }

    @Override
    public String toString() {
        return varname;
    }

    public String getVarname() {
        return this.varname;
    }

    public void setVarname(String varname) {
        this.varname = varname;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

//    public List<StatementNode> getStatementsUsed() {
//        return this.statementsUsed;
//    }
//
//    public void setStatementsUsed(List<StatementNode> statementsUsed) {
//        this.statementsUsed = statementsUsed;
//    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public void addStatement(StatementNode obj) {
//        this.statementsUsed.add(obj);
//    }
//
//    public void replaceStatementsUsed(StatementNode assignmentStmNode) {
//        this.statementsUsed.remove(this.statementsUsed.size() -1);
//        this.statementsUsed.add(assignmentStmNode);
//
//    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
