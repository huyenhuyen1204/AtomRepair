package AST.stm.node;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class ArrayInitializerNode extends StatementNode {
    {
        this.nodeType = NodeType.ArrayInitializerNode;
    }
    public ArrayInitializerNode(ASTNode astNode, int line, String classfullName) {
        super();
        this.line = line;
        setFullNameParent(classfullName);
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
//        this.nodeType = astNode.getNodeType();
    }
}
