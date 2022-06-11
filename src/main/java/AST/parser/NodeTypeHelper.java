package AST.parser;
import AST.stm.token.InfixExpressionStmNode;
import AST.stm.token.MethodInvocationStmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JavaLibraryHelper;

public class NodeTypeHelper {
    public static final Logger logger = LoggerFactory.getLogger(NodeTypeHelper.class);

//    public static Object getValueExpected(Object obj, int line.txt) {
//                 if (obj instanceof InfixExpressionStmNode) {
//            String value = getValueInfixExpression((InfixExpressionStmNode) obj);
//            return new StringNode(line.txt, null, value, obj.toString());
//        } else {
//            return obj;
//        }
//    }

    private static String getValueInfixExpression(InfixExpressionStmNode statementNode) {
        Object left = statementNode.getLeft();
        String value = "";
        value = getValueObject(left);
        Object right = statementNode.getRight();
        if (statementNode.getOperator().equals("+")) {
            value += getValueObject(right);
        } else {
            logger.error("Chua xu ly:getValueInfixExpression operator" + statementNode.getOperator());
        }
        for (Object o : statementNode.getExtendedOperands()) {
            if (statementNode.getOperator().equals("+")) {
                value += getValueObject(o);
            } else {
                logger.error("Chua xu ly:getValueInfixExpression operator" + statementNode.getOperator());
            }
        }
        return value;

    }

    private static String getValueObject(Object obj) {
        if (obj instanceof String) {
            obj = JavaLibraryHelper.removeFirstAndLastChars((String) obj);
            return (String) obj;
        } else if (obj instanceof MethodInvocationStmNode) {
            logger.error("Khong xu ly:getValueObject ");
            return null;
        } else {
            logger.error("Chuwa xu lys:getValueInfixExpression " + obj);
            return null;
        }
    }
}
