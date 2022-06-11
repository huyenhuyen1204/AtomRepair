package AST.stm.abst;

import AST.node.MethodNode;
import AST.stm.token.BaseVariableNode;
import AST.stm.token.MethodCalledNode;
import AST.stm.token.MethodInvocationStmNode;
import main.core.token.Token;
import util.ReflectionHelper;

import java.util.ArrayList;
import java.util.List;

public class StatementNode {
    protected int line;
    protected String statementString;
    protected int startPostion;
    protected int endPostion;
    protected StatementNode parent;
    protected String lparen = "";
    protected String rparen = "";
    protected List<StatementNode> children;
    public Boolean isSameMethod = null; // for base var
    public Boolean findInClass = null; // for MethodCalled

    protected StatementNode () {
        children = new ArrayList<>();
    }


    public int getStmBugDeepLevel() {
        return stmBugDeepLevel;
    }

    public String getLparen() {
        return lparen;
    }

    public void setLparen(String lparen) {
        this.lparen = lparen;
    }

    public String getRparen() {
        return rparen;
    }

    public void setRparen(String rparen) {
        this.rparen = rparen;
    }

    /**
     * set bug deep level for itself and its children.
     * @param statementNode current statement node
     * @param stmBugDeepLevel bug deep level
     */
    public void setStmBugDeepLevel(StatementNode statementNode, int stmBugDeepLevel) {
        statementNode.stmBugDeepLevel = stmBugDeepLevel;
        if (this.getChildren()!= null) {
            for (StatementNode child : this.getChildren()) {
                child.setStmBugDeepLevel(child, stmBugDeepLevel);
            }
        }
    }
    protected  int deepLevel = 0;
    protected int stmBugDeepLevel = 0;
    protected String type = null;
    public String fullNameParent = null;
    public String cast = "";
    public Integer paramSize = null;

    // for generate candidate
    private String prefix = null;
    private String suffix = null;
    protected Token typeToken = null;
    protected int tokeneLevel = 0;

    public NodeType nodeType;

    protected NodeInstance nodeInstance = NodeInstance.NORMAL;

    public Token getToken() {
        return typeToken;
    }

    public void setToken(Token typeToken) {
        this.typeToken = typeToken;
    }

    public String getPrefix() {
        if (prefix == null) {
            if (parent instanceof MethodCalledNode || parent instanceof BaseVariableNode) {
                String parentPrefix = parent.getPrefix();
                if (!parentPrefix.equals(""))
                    prefix = parent.getPrefix() + "." + parent.toString();
                else
                    prefix = parent.toString();
            } else
                prefix = "";
        }
        return cast + lparen + prefix;
    }

    public String getSuffix() {
        if (suffix == null) {
            if (children.size() == 0) {
                suffix = "";
            } else {
                String suffixChild = children.get(0).getSuffix();
                if (suffixChild.equals("")) {
                    suffix = children.get(0).toString();
                } else {
                    suffix = children.get(0).toString() + "."
                            + children.get(0).getSuffix();
                }
            }
        }
        String newStr = suffix + rparen;
        return newStr;
    }

    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public void setNodeInstance(NodeInstance nodeInstance) {
        if (nodeInstance == NodeInstance.INFIX) {
                recursive(this, nodeInstance);
        }  else if (nodeInstance == NodeInstance.ARGUMENT) {
            recursive(this, nodeInstance);
        } else {
            this.nodeInstance = nodeInstance;
        }
    }
    public void setParamSize(int size) {
        recursiveParam(this, size);
    }

    private void recursiveParam(StatementNode statementNode, int size) {
        statementNode.paramSize = size;
        for (StatementNode child : statementNode.getChildren()) {
            recursiveParam(child, size);
        }
    }

    public void setInstance(NodeInstance nodeInstance) {
        this.nodeInstance = nodeInstance;
    }


    public void recursive (StatementNode stm, NodeInstance nodeInstance) {
        stm.setInstance(nodeInstance);
        for (StatementNode child : stm.getChildren()) {
            recursive(child, nodeInstance);
        }
    }

    public List<StatementNode> getChildren() {
        return this.children;
    }

    public StatementNode getParent() {
        return this.parent;
    }


    public void setParent(StatementNode parent) {
        this.parent = parent;
    }

    public int getStartPostion() {
        return startPostion;
    }

    public void setStartPostion(int startPostion) {
        this.startPostion = startPostion;
    }

    public int getEndPostion() {
        return endPostion;
    }

    public void setEndPostion(int endPostion) {
        this.endPostion = endPostion;
    }

    public void clearChildren() {
        this.children = new ArrayList<>();
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    @Override
    public String toString() {
        String strString = cast + lparen + this.statementString + rparen;
        return strString;
    }

    public String getString() {
        return this.statementString;
    }

    public void setStatementString(String statementString) {
        this.statementString = statementString;
    }

    public void setChild(StatementNode child, MethodNode methodNode) {
        String childType = null;
        if (child.getType() == null) {
            if (child instanceof BaseVariableNode) {
                if (this.getType() != null) {
                    childType = ReflectionHelper.findFieldType(this.getType(),
                            ((BaseVariableNode) child).getKeyVar(), methodNode);
                } else {
                    if (this.fullNameParent != null) {
                        childType = ReflectionHelper.findFieldType
                                (this.fullNameParent,
                                        ((BaseVariableNode) child).getKeyVar(), methodNode);
                    } else {
//                        System.out.println();
                    }
                }
            } else if (child instanceof MethodCalledNode) {
                childType = ReflectionHelper.findMethodCalledNodeType((MethodCalledNode) child, this);
            }
            if (childType != null) {
                child.setType(childType);
            } else {
//                System.out.println("type == null");
            }
        }
        this.children.add(child);
    }

    public void setFullNameParent(String fullNameParent) {
        this.fullNameParent = fullNameParent;
    }

    public int getDeepLevel() {
        return this.deepLevel;
    }

    public void setDeepLevel(int deepLevel) {
        setDeepLevel(this, deepLevel);
    }

    private void setDeepLevel(StatementNode statementNode, int dl) {
        this.deepLevel = dl;
        if (statementNode instanceof MethodCalledNode) {
            for (StatementNode arg : ((MethodCalledNode) statementNode).getArguments()) {
                setDeepLevel(arg, dl);
            }
        }
        if (statementNode.getChildren().size() > 0) {
            for (StatementNode child : statementNode.getChildren()) {
                setDeepLevel(child, dl);
            }
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (this instanceof MethodInvocationStmNode) {
            ((MethodInvocationStmNode) this).getLastChild(this).setType(type);
            ((MethodInvocationStmNode) this).getNodes().get(((MethodInvocationStmNode) this)
                    .getNodes().size() - 1).setType(type);
        }
    }

    public StatementNode getLastChild (StatementNode child) {
        if (child.getChildren().size() > 0) {
            return getLastChild(child.getChildren().get(0));
        } else {
            return child;
        }
    }


}
