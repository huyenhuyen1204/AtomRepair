package AST.stm.token;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import util.ASTHelper;

import java.util.ArrayList;

public class BaseVariableNode extends StatementNode implements Token {
    private String keyVar;

    {
        this.nodeType = NodeType.BaseVariableNode;
    }
    public BaseVariableNode(String keyVar) {
        super();
        this.keyVar = keyVar;
        this.setStatementString(keyVar);
    }

    public BaseVariableNode(SimpleName stmNode, String type, int  line, String classfullName) {
        super();
        this.keyVar = stmNode.getIdentifier();
        Position position = ASTHelper.getPosition(stmNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = stmNode.toString();
        this.line = line;
        this.type = type;
        this.nodeType = NodeType.BaseVariableNode;
    }

    public BaseVariableNode(Name stmNode, String type, int  line, String classfullName) {
        super();
        setFullNameParent(classfullName);
        this.keyVar = stmNode.getFullyQualifiedName();
        Position position = ASTHelper.getPosition(stmNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = stmNode.toString();
        this.line = line;
        this.type = type;
        this.children = new ArrayList<>();
//        this.nodeType = stmNode.getNodeType();
        this.nodeType = NodeType.BaseVariableNode;
    }

    public BaseVariableNode(String keyVar, String classFullName, int  line, String classfullName) {
        super();
        setFullNameParent(classFullName);
        this.keyVar = keyVar;
        this.statementString = keyVar;
        this.line = line;
        this.type = classFullName;
        this.children = new ArrayList<>();
//        if (astNode != null) {
////            this.nodeType = astNode.getNodeType();
//        }
        this.nodeType = NodeType.BaseVariableNode;
    }

    public BaseVariableNode(Name stmNode, String varname,
                            String typevar, CompilationUnit cu, String classfullName) {
        super();
        setFullNameParent(classfullName);
        this.keyVar = varname;
        Position position = ASTHelper.getPosition(stmNode);
        this.startPostion = position.getStartPos();
        this.endPostion = position.getEndPos();
        this.statementString = stmNode.toString();
        this.type = typevar;
        this.line = ASTHelper.getLine(stmNode, cu);
//        this.nodeType = stmNode.getNodeType();
        this.children = new ArrayList<>();
        this.nodeType = NodeType.BaseVariableNode;
    }


    public String getKeyVar() {
        return this.keyVar;
    }

    public void setKeyVar(String keyVar) {
        this.keyVar = keyVar;
    }



    @Override
    public NodeType getObject() {
        return NodeType.BaseVariableNode;
    }

    @Override
    public int getHashCode() {
        if (type != null) {
            return (type +keyVar).hashCode();
        } else  if (keyVar == null) {
            return "null".hashCode();
        } else {
            return keyVar.hashCode();
        }
    }

    @Override
    public String toString() {
        return keyVar;
    }
}
