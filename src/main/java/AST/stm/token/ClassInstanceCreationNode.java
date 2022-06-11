package AST.stm.token;

import AST.obj.Position;
import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;
import org.eclipse.jdt.core.dom.ASTNode;
import util.ASTHelper;

import java.util.ArrayList;
import java.util.List;

public class ClassInstanceCreationNode extends StatementNode implements Token {
    private String name;
    private List<StatementNode> args;
    {
        this.nodeType = NodeType.ClassInstanceCreationNode;
    }
    public ClassInstanceCreationNode(String type, List<StatementNode> args,
                                     ASTNode astNode, int line, String classfullName) {
        super();
        setFullNameParent(classfullName);
        this.name = type;
        this.setType(type);
        if (astNode != null) {
            Position position = ASTHelper.getPosition(astNode);
            this.startPostion = position.getStartPos();
            this.endPostion = position.getEndPos();
            this.statementString = astNode.toString();
        }

        this.line = line;
        this.args = new ArrayList<>();
        this.args.addAll(args);
        //set child
//        children = args;
        //set parent
//        for (StatementNode stm : args) {
//            if (stm != null) {
//                stm.setParent(this);
//            }
//        }
//        this.nodeType = astNode.getNodeType();
        this.nodeType = NodeType.ClassInstanceCreationNode;
    }

    public ClassInstanceCreationNode (String name, String type) {
        this.children = new ArrayList<>();
        this.args = new ArrayList<>();
        this.setType(type);
        this.name = name;
        this.nodeType = NodeType.ClassInstanceCreationNode;
        this.statementString = null;
    }

    public void addParam (StatementNode statementNode) {
        this.args.add(statementNode);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StatementNode> getArgs() {
        return args;
    }

    public void setArgs(List<StatementNode> args) {
        this.args = args;
    }

    @Override
    public String toString () {
        if (this.statementString == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("new ").append(name.replace("$", ".")).append("(");
            for (StatementNode param : args) {
                builder.append(param.toString());
                if (args.size() != args.indexOf(param) + 1) {
                    builder.append(",");
                }
            }
            builder.append(")");
            this.statementString = builder.toString() ;
        }
        return this.statementString;
    }

    @Override
    public NodeType getObject() {
        return NodeType.ClassInstanceCreationNode;
    }

    @Override
    public int getHashCode() {
        String params = "";
        if (args != null) {
            for (StatementNode param : args) {
                if (param instanceof Token) {
                    params += param.getType();
                } else {
                    if (param != null) {
                        params += param.toString();
                    } else {
                        params += "";
                    }
                }
            }
        }
        return (name + params).hashCode();
    }
}
