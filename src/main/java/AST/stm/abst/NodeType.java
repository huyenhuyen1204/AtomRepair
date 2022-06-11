package AST.stm.abst;

/**
 * This token can fix Bug
 */
public enum NodeType {
    BaseVariableNode, BooleanNode, MethodCalledNode, QualifiedNameNode,
    ArrayAccessNode,ArrayCreationNode,AssignmentNode, ClassInstanceCreationNode, ExpressionNode,
    IfStmNode, ConstructorInvocationNode, NumbericNode, StringNode, UndefinedNode, ArrayInitializerNode,
    // just getOperator
    MethodInvocationStmNode, OperatorNode, InfixExpressionStmNode


}
