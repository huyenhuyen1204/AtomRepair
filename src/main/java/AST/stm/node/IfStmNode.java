package AST.stm.node;

import AST.obj.Position;
import AST.stm.abst.NodeInstance;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class IfStmNode extends StatementNode  {
    // children: infix, then, if else, else
    private StatementNode infix;
    public IfStmNode(StatementNode infix, ASTNode astNode, int line) {
        super();
        this.infix = infix;
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.line = line;
        this.statementString = astNode.toString();
        if (infix != null) {
            this.infix.setParent(this);
            this.infix.setNodeInstance(NodeInstance.NORMAL);
            children.add(infix);
        }
        this.nodeType = NodeType.IfStmNode;
    }

}
