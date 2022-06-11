package AST.stm.token;

import AST.node.MethodNode;
import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * eg: that.toolTipText
 * with 'that' : org.jfree.chart.annotations.AbstractXYAnnotation
 * toolTipText: String in class of 'that'
 */
public class QualifiedNameNode extends StatementNode implements Token {
    //    private BaseVariableNode qualifier;
//    private BaseVariableNode name;
    private List<BaseVariableNode> baseVariableNodes;

    {
        this.nodeType = NodeType.QualifiedNameNode;
    }

    public QualifiedNameNode(BaseVariableNode qualifier,
                             BaseVariableNode name, ASTNode astNode, int line) {
        super();
        baseVariableNodes = new ArrayList<>();
//        this.qualifier = qualifier;
//        this.name = name;
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
        this.line = line;
//        this.children.add(name);
//        this.children.add(qualifier);
        //add parent

        if (qualifier != null) {
            qualifier.setParent(this);
        }
        if (name != null) {
            name.setParent(this);
        }
        this.type = name.getType();
//        this.nodeType = astNode.getNodeType();
        this.nodeType = NodeType.QualifiedNameNode;
    }

    public QualifiedNameNode(BaseVariableNode qualifier, BaseVariableNode name) {
        super();
        this.baseVariableNodes = new ArrayList<>();
        this.baseVariableNodes.add(qualifier);
        this.baseVariableNodes.add(name);
        this.setType(name.getType());
    }


    public QualifiedNameNode(List<BaseVariableNode> baseVariableNodes,
                             ASTNode astNode, int line, String classfullName,
                             MethodNode methodNode) {
        super();
        this.baseVariableNodes = new ArrayList<>();
        this.baseVariableNodes.addAll(baseVariableNodes);
        setFullNameParent(classfullName);
//        this.qualifier = qualifier;
//        this.name = name;
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
        this.line = line;
        //add child
        BaseVariableNode node = baseVariableNodes.get(0);
        this.setChild(node, methodNode);
        node.setParent(this);
        for (int i = 1; i < baseVariableNodes.size(); i++) {
            BaseVariableNode baseVariableNode = baseVariableNodes.get(i);
            baseVariableNode.setParent(node);
            node.setChild(baseVariableNode, methodNode);
            node = baseVariableNode;
            if (this.getType() == null) {
                if (this.baseVariableNodes.get(this.baseVariableNodes.size() - i).getType() != null) {
                    this.setType(this.baseVariableNodes.get(this.baseVariableNodes.size() - i).getType());
                }
            }
        }
        for (int i = 1; i <= baseVariableNodes.size(); i++) {
            if (this.getType() == null) {
                if (this.baseVariableNodes.get(this.baseVariableNodes.size() - i).getType() != null) {
                    this.setType(this.baseVariableNodes.get(this.baseVariableNodes.size() - i).getType());
                }
            }
        }

//        this.children.add(name);
//        this.children.add(qualifier);
        //add parent
//        if (name != null) {
//            name.setParent(this);
//        }
//        if (qualifier != null) {
//            qualifier.setParent(this);
//        }
//        this.type = name.getType();
//        this.nodeType = astNode.getNodeType();
        this.nodeType = NodeType.QualifiedNameNode;
    }


//    public BaseVariableNode getQualifier() {
//        return qualifier;
//    }

//    public void setQualifier(BaseVariableNode qualifier) {
//        this.qualifier = qualifier;
//    }
//
//    public BaseVariableNode getName() {
//        return name;
//    }
//
//    public void setName(BaseVariableNode name) {
//        this.name = name;
//    }


    public List<BaseVariableNode> getBaseVariableNodes() {
        return baseVariableNodes;
    }

//    public void setBaseVariableNodes(List<BaseVariableNode> baseVariableNodes) {
//        this.baseVariableNodes = baseVariableNodes;
//    }

    @Override
    public NodeType getObject() {
        return null;
    }

    public void setType(String type) {
        this.type = type;
//        this.name.setType(type);
    }

    @Override
    public int getHashCode() {
        String hash = "";
        for (BaseVariableNode baseVariableNode : baseVariableNodes) {
            if (baseVariableNode.getType() != null) {
                hash += baseVariableNode.getType();
            } else {
                hash += "null";
            }
        }
        return hash.hashCode();
    }

}
