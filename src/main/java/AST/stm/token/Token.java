package AST.stm.token;

import AST.stm.abst.NodeType;

/**
 * Object is Token can replace to fix program
 */
public interface Token {
    NodeType getObject();
    int getHashCode();
}
