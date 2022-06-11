package AST.stm.token;

import AST.node.MethodNode;
import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import util.ASTHelper;
import util.ReflectionHelper;

import java.util.ArrayList;
import java.util.List;

public class MethodInvocationStmNode extends StatementNode implements Token {
    //    public static final String nodeType = "MethodInvocation";

    {
        this.nodeType = NodeType.MethodInvocationStmNode;
    }
    private String methodType;
//    private String keyVar;
//    private BaseVariableNode baseVar;
//    private String methodCalled = null; // eg: student.getname() - method called is getname
    //for list method call eg: customerList.get(0).toString()
    // methods called: get, tostring
    private List<StatementNode> nodes = null; //customerList.get(0).toString() -> toString, get

    public MethodInvocationStmNode(StatementNode onlyChilld) {
        this.nodes = new ArrayList<>();
        this.statementString = onlyChilld.toString();
        this.getChildren().add(onlyChilld);
        addNode(onlyChilld);
    }

    public MethodInvocationStmNode(ASTNode methodInvocation, int line, String classfullName) {
        super();
        this.nodes = new ArrayList<>();
        this.line = line;
        this.statementString = methodInvocation.toString();
        Position position = ASTHelper.getPosition(methodInvocation);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.nodeType = NodeType.MethodInvocationStmNode;
        setFullNameParent(classfullName);
    }

    public void setChildren(String classType, CompilationUnit cu, MethodNode methodNode) {
//        Collections.reverse(nodes);
        if (nodes.get(0) instanceof MethodCalledNode) {
            List<String> params = new ArrayList<>();
            for (StatementNode param : ((MethodCalledNode) nodes.get(0)).getArguments()) {
                params.add(param.getType());
            }
            String methodType = ReflectionHelper.findMethodType(classType,
                    ((MethodCalledNode) nodes.get(0)),
                    params, methodNode, cu);
            nodes.get(0).setType(methodType);

//            this.getChildren().get(0).setType(method.getReturnType().getName());
        }
        if (this.methodType != null) { // setLast elemt
            if (nodes.get(nodes.size() - 1) instanceof Token) {
                if ((nodes.get(nodes.size() - 1)).getType() == null) {
                    (nodes.get(nodes.size() - 1)).setType(this.methodType);
                } else {
                    this.methodType = (nodes.get(nodes.size() - 1)).getType();
                }
            }
        }

        // set Child
//         if (baseVar == null) { //is super
//             BaseVariableNode baseVariableNode = new BaseVariableNode((String) null, null, this.line);
//             this.baseVar = baseVariableNode;
//         } else {
//            baseVar.setParent(this);
//        }
//        this.children.add(baseVar);
        StatementNode parentStmNode = this;
        for (StatementNode mt : nodes) {
            //set Child
            if (parentStmNode != null) {
                if (mt != null) {
                    parentStmNode.setChild(mt, methodNode);
                }
            }
            //setParent
            if (mt != null) {
                mt.setParent(parentStmNode);
            }
            parentStmNode = mt;
        }
        if (this.methodType == null) { //set parent Type
            if (getLastChild(this) instanceof Token) {
                this.methodType = (getLastChild(this)).getType();
            }
        }
    }

//    public void addMethodCall(MethodCalledNode methodCalledNode) {
//        this.nodes.add(methodCalledNode);
//        //final methodCall is type Of methodCall
////        this.methodType = ((MethodCalledNode)this.methodsCalled.get(this.getMethodsCalled().size() -1)).getMethodType();
//    }

    public List<StatementNode> getNodes() {
        return nodes;
    }


//
//    public BaseVariableNode getBaseVar() {
//        return baseVar;
//    }
//
//    public void setBaseVar(BaseVariableNode baseVar) {
//        this.baseVar = baseVar;
//    }


    @Override
    public NodeType getObject() {
        return NodeType.MethodInvocationStmNode;
    }

    @Override
    public void setType(String type) {
//        if (this.type == null) {
        this.methodType = type;
//        }
        StatementNode statementNode = getLastChild(this);
        if (statementNode != this) {
            if (type != null) {
                statementNode.setType(type);
            }
        }
//        if (nodes.size() > 0) {
//            if ((nodes.get(nodes.size() - 1)).getType() == null) {
//                (nodes.get(nodes.size() - 1)).setType(type);
//            }
//        }
    }

    @Override
    public int getHashCode() {
        String hash = "";
//        if (baseVar != null) {
//           hash = baseVar.getType();
//        }
        for (StatementNode stm : nodes) {
            if (stm instanceof Token) {
                hash += ((Token) stm).getHashCode();
            }
        }

        return hash.hashCode();
    }


//    public void addChild(StatementNode node) {
//        this.children.add(node);
//    }

    public void addNode(StatementNode node) {
        if (node instanceof QualifiedNameNode) {
            for (StatementNode statementNode: ((QualifiedNameNode) node).getBaseVariableNodes()) {
                statementNode.clearChildren();
                this.nodes.add(statementNode);
            }
        } else {
            this.nodes.add(node);
        }
    }

    public void setChildType(StatementNode statementNode, MethodNode methodNode) {
        if (this.getChildren().size() > 0) {
//            this.getChildren().get(0).setChild(statementNode);
            //final child to add
            addFinalChild(this, statementNode, methodNode);
            this.nodes.add(statementNode);
        } else if (this.getChildren().size() == 0) {
            this.children.add(statementNode);
            this.nodes.add(statementNode);
        }
    }

    private void addFinalChild (StatementNode child, StatementNode nodeadd, MethodNode methodNode) {
        if (child.getChildren().size() > 0) {
            addFinalChild(child.getChildren().get(0), nodeadd, methodNode);
        } else {
            child.setChild(nodeadd, methodNode);
        }
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}
