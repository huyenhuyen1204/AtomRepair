package AST.stm.node;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class ExpressionNode extends StatementNode {
    private String type;
    private StatementNode leftNode;
    private StatementNode rightNode;

    public ExpressionNode(StatementNode leftNode, StatementNode rightNode,
                          String type, ASTNode astNode, int line, String classfullName) {
        super();
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        setFullNameParent(classfullName);
        this.type = type;
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.line = line;
        this.statementString = astNode.toString();
        //set parent
        if (leftNode != null) {
            leftNode.setParent(this);
            children.add(leftNode);
        }
        if (rightNode != null) {
            rightNode.setParent(this);
            children.add(rightNode);
        }
        this.nodeType = NodeType.ClassInstanceCreationNode;
    }

}
