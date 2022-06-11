package AST.stm.nodetype;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

public class StringNode extends StatementNode {
//    public static final String nodeType = "StringLiteral";
    private String value;
    private String keyVar;
    public StringNode(int line, String keyVar, String value,
                      String stmString, ASTNode astNode, String classfullName) {
        super();
        this.line = line;
        this.keyVar = keyVar;
        this.statementString = stmString;
        this.value = value;
        setFullNameParent(classfullName);
        Position position = ASTHelper.getPosition(astNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = astNode.toString();
//        this.nodeType = astNode.getNodeType();
//        if (value.startsWith("\"") && value.endsWith("\"")) {
            this.setType("java.lang.String");
//        }
//        else if (value.startsWith("'") && value.endsWith("'")) {
//            this.setType("char");
//        }
        this.nodeType = NodeType.StringNode;
//        this.setType("java.lang.String");
    }

}
