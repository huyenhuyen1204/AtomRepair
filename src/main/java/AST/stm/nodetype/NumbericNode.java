package AST.stm.nodetype;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class NumbericNode extends StatementNode {
//    public static final String nodeType = "NumberLiteral";
    private Object numberic;

    public NumbericNode(Object numberic, ASTNode astNode, String classfullName) {
        super();
        this.numberic = numberic;
        setFullNameParent(classfullName);
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
        this.nodeType = NodeType.NumbericNode;
    }

}
