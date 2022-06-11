package AST.stm.node;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class ArrayAccessNode extends StatementNode{
    private StatementNode arrayExpression;
    private StatementNode indexExpression;
    {
        this.nodeType = NodeType.ArrayAccessNode;
    }
    public ArrayAccessNode(StatementNode arrayExpression,
                           StatementNode indexExpression, ASTNode astNode,
                           int line, String classQualifiedName) {
        super();
        this.fullNameParent = classQualifiedName;
        this.arrayExpression = arrayExpression;
        this.indexExpression = indexExpression;
        this.line = line;
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();

        //set parent
        if (arrayExpression != null) {
            arrayExpression.setParent(this);
            children.add(arrayExpression);
        }
//        if (indexExpression != null) {
//            indexExpression.setParent(this);
//            children.add(indexExpression);
//        }
        this.nodeType = NodeType.ArrayAccessNode;
    }


}
