package AST.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.jdt.core.dom.ASTNode;


public class JavaNode extends Node {

    @JsonProperty("isFinal")
    protected boolean isFinal = false;

    @JsonIgnore
    protected ASTNode astNode;

//    @JsonProperty("isFinal")
//    public boolean isFinal() {
//        return isFinal;
//    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public void setAstNode(ASTNode astNode) {
        this.astNode = astNode;
    }

    //    protected NodePosition startPosition;
//    protected NodePosition endPosition;
//
//    public static class NodePosition implements Serializable {
//        private int lineNumber;
//        private int columnNumber;
//
//        public NodePosition(int lineNumber, int columnNumber) {
//            this.lineNumber = lineNumber;
//            this.columnNumber = columnNumber;
//        }
//
//        public int getLineNumber() {
//            return lineNumber;
//        }
//
//        public int getColumnNumber() {
//            return columnNumber;
//        }
//    }
}
