package AST.stm.node;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class AssignmentNode extends StatementNode {
    private StatementNode leftSide;
    private StatementNode rightNode;
    private String assignmentOperator;
    {
        this.nodeType = NodeType.AssignmentNode;
    }
    public AssignmentNode(String operator, StatementNode leftSide,
                          StatementNode rightNode, int line, ASTNode astNode, String classfullName) {
        super();
        setFullNameParent(classfullName);
//        this.nodeType = astNode.getNodeType();
        this.assignmentOperator = operator;
        this.leftSide = leftSide;
        this.rightNode = rightNode;
        this.line = line;
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
        //set parent
        if (leftSide != null) {
            leftSide.setParent(this);
            children.add(leftSide);
        }
        if (rightNode != null) {
            rightNode.setParent(this);
            children.add(rightNode);
        }
        this.nodeType = NodeType.AssignmentNode;
    }

}
