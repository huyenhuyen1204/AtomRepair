package AST.stm.node;

import AST.stm.abst.NodeType;
import AST.stm.abst.StatementNode;

public class OperatorNode extends StatementNode {
    private  String operator;
    {
        this.nodeType = NodeType.OperatorNode;
    }
    public OperatorNode(String operator, int startPos, int endPos) {
        super();
        this.operator = operator;
        this.statementString =  operator;
        if (startPos != -1) {
            this.startPostion = startPos;
        } else {
            this.startPostion = endPos;
        }
        if (endPos != -1) {
            this.endPostion = endPos;
        } else {
            this.endPostion = startPos;
        }
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
