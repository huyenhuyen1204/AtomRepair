package AST.stm.token;

import AST.node.ParameterNode;
import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import AST.stm.node.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

import java.util.ArrayList;
import java.util.List;

public class MethodCalledNode extends StatementNode implements Token {
//    private String methodType;
    private String methodName;
    private List<StatementNode> agurementTypes = null;

    {
        this.nodeType = NodeType.MethodCalledNode;
    }

    /**
     * For testing. Minor
     * @param methodName
     */
    public MethodCalledNode(String methodName) {
        super();
        this.methodName = methodName;
        this.agurementTypes = new ArrayList<>();
    }

    public void addParameterParams(List<ParameterNode> stmParams) {
        for (ParameterNode param : stmParams) {
            TypeNode typeNode = new TypeNode(param.getType());
//            typeNode.setStmBugDeepLevel( typeNode,this.stmBugDeepLevel + 1);
            this.agurementTypes.add(typeNode);
        }
    }

    public MethodCalledNode(String methodName, List<StatementNode> agurementTypes,
                            ASTNode astNode, int line, String classfullName) {
        super();
//        if (this.getParent() == null) {
//            BaseVariableNode baseVariableNode = new BaseVariableNode();
//            this.setParent(baseVariableNode);
//        }
        setFullNameParent(classfullName);
        this.methodName = methodName;
        this.agurementTypes = new ArrayList<>();
        if (agurementTypes.size() > 0) {
//            for (StatementNode arg: agurementTypes) {
//                arg.setStmBugDeepLevel(arg, this.stmBugDeepLevel + 1);
//            }
            this.agurementTypes.addAll(agurementTypes);
        }
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
        this.line = line;
//        this.nodeType = astNode.getNodeType();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<StatementNode> getArguments() {
        return agurementTypes;
    }

    public void setAgurementTypes(List<StatementNode> agurementTypes) {
//        for (StatementNode arg: agurementTypes) {
//            arg.setStmBugDeepLevel(arg,this.stmBugDeepLevel + 1);
//        }
        this.agurementTypes = agurementTypes;
    }

//    public String getMethodType() {
//        return methodType;
//    }

//    public void setMethodType(String methodType) {
//        this.methodType = methodType;
//    }

    @Override
    public NodeType getObject() {
        return NodeType.MethodCalledNode;
    }

    @Override
    public String getType() {
        return type;
    }


    @Override
    public int getHashCode() {
        String params = "";
        if (agurementTypes != null) {
            for (StatementNode param : agurementTypes) {
                if (param instanceof Token) {
                    params += param.getType();
                } else {
                    if (param != null) {
                        params += param.toString();
                    } else {
                        params += "";
                    }
                }
            }
        }
        return (methodName + params).hashCode();
    }

    public void setDeepLevel(int deepLevel) {
        this.deepLevel = deepLevel;
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

    @Override
    public String toString() {
//        if (statementString != null) return statementString;
        String builder;
        builder = methodName + "(";
        for (int i = 0; i < agurementTypes.size(); i++) {
            StatementNode stm = agurementTypes.get(i);
            builder += stm.getCast() + stm.getLparen() + stm.toString() + stm.getRparen();

//            if (stm instanceof Token) {
//
//                builder.append(stm);
//            } else {
//                builder.append(stm.getStatementString());
//            }
            if (i < agurementTypes.size() - 1)
                builder += ",";
        }
        builder += ")";
        statementString = builder;
        return statementString;
    }
}
