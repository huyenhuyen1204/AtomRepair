package AST.stm.nodetype;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class UndefinedNode extends StatementNode {
    private String undefindQualityType;

    public UndefinedNode(String undefindNode, ASTNode astNode, int line, String classfullName) {
        super();
        this.undefindQualityType = undefindNode;
        setFullNameParent(classfullName);
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
        this.line = line;
//        this.nodeType = astNode.getNodeType();
        this.nodeType = NodeType.UndefinedNode;
    }

}
