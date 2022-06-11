package AST.node;

import AST.obj.Position;
import AST.stm.abst.NodeInstance;
import AST.stm.abst.StatementNode;
import AST.stm.node.*;
import AST.stm.nodetype.InitNode;
import AST.stm.nodetype.NumbericNode;
import AST.stm.nodetype.StringNode;
import AST.stm.nodetype.UndefinedNode;
import AST.stm.token.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ASTHelper;
import util.Checker;
import util.ReflectionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MethodNode extends JavaNode {
    public static final Logger logger = LoggerFactory.getLogger(MethodNode.class);
    // protected boolean isAbstract;
    protected String returnType;
    protected List<Node> parameters;
    private String simpleName;
    private boolean isConstructor = false;
    private int startLine;
    private int endLine;
    private ClassNode classNode;
    //    private boolean returnStringOrNumber = true;
    List<InitNode> initNodes;
    List<StatementNode> returnStatements;
    private static CompilationUnit cu;
    @JsonIgnore
    private List statements;
    private List<StatementNode> statementNodes;
    public List<Node> innerclasses;

    public List getStatements() {
        return statements;
    }

    public void setStatements(List statements) {
        this.statements = statements;
    }

    public MethodNode() {
        this.initNodes = new ArrayList<>();
        this.returnStatements = new ArrayList<>();
        parameters = new ArrayList<>();
        this.statementNodes = new ArrayList<>();
    }

    public List<StatementNode> getStatementNodes() {
        return statementNodes;
    }

    public void setStatementNodes(List<StatementNode> statementNodes) {
        this.statementNodes = statementNodes;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<InitNode> getInitNodes() {
        return initNodes;
    }

    public void setInitNodes(List<InitNode> initNodes) {
        this.initNodes = initNodes;
    }

    public List<StatementNode> getReturnStatements() {
        return returnStatements;
    }

    public void setReturnStatements(List<StatementNode> returnStatements) {
        this.returnStatements = returnStatements;
    }

    @Override
    public int getStartLine() {
        return startLine;
    }

    @Override
    public void setStartLine(int line) {
        this.startLine = line;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public void setEndLine(int line) {
        this.endLine = line;
    }

    public List<ParameterNode> getParameters() {
        ArrayList<ParameterNode> list = new ArrayList<>();
        for (Node node : this.getChildren()) {
            if (node instanceof ParameterNode) {
                list.add((ParameterNode) node);
            }
        }
        return list;
    }

    public void setParameters(List list) {
        this.parameters = list;
    }

    public void addParameter(ParameterNode param) {
        if (param != null) parameters.add(param);
    }

    public void addAllParameters(List<Node> params) {
        parameters.addAll(params);
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    @JsonProperty("isConstructor")
    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean constructor) {
        isConstructor = constructor;
    }

    @Override
    public String toString() {
        return "MethodNode{" +
                "id=" + this.id +
//                "visibility=" + this.getVisibility() +
                ", returnType='" + returnType + '\'' +
                ", name='" + name + '\'' +
//                ", isStatic=" + this.isStatic() +
//                ", isAbstract=" + this.isAbstract() +
//                ", isFinal=" + this.isFinal() +
                ", parameters=" + parameters +
                '}';
    }

    public void setInforFromASTNode(ClassNode classNode, MethodDeclaration node, CompilationUnit cu) {
        this.cu = cu;
        this.classNode = classNode;
        if (node.getName() != null) {
            if (node.getName().getIdentifier() != null) {
                this.name = node.getName().getIdentifier();
            }
        }
        List statements = null;
        if (node.getBody() != null) {
            statements = node.getBody().statements();
        }
        this.setStatements(statements);

        //set ten cho phuong thuc
        this.setStartPosition(node.getStartPosition());
        int nodeLength = node.getLength();
        this.setStartLine(this.cu.getLineNumber(node.getStartPosition()));
        this.setStartPosition(node.getStartPosition());
        this.setEndPosition(node.getStartPosition() + nodeLength);
        int endLineNumber = this.cu.getLineNumber(node.getStartPosition() + nodeLength) - 1;
        this.setEndLine(endLineNumber);

        if (node.isConstructor() == false) {
            Type s = node.getReturnType2();
            if (s != null) {
                this.setReturnType(ASTHelper.getFullyQualifiedName(this.classNode, s, this.cu));
            }
        } else {
            this.isConstructor = true;
            this.setReturnType("");
        }

        List parameters = node.parameters();
        List<Node> paraNodes = new ArrayList<>();
        for (Object o : parameters) {
            ParameterNode paraNode = new ParameterNode();
            if (o instanceof SingleVariableDeclaration) {
                paraNode.setName(((SingleVariableDeclaration) o).getName().getIdentifier());
                String simpleType = ((SingleVariableDeclaration) o).getType().toString();
                String fullType = ASTHelper.getFullyQualifiedName(
                        this.classNode, ((SingleVariableDeclaration) o).getType(), cu);
                if (o.toString().contains(simpleType + "... ")) {
                    fullType += "[]";
                }
                paraNode.setType(fullType);
                paraNode.setStartLine(ASTHelper.getLine((ASTNode) o, cu));
                paraNodes.add(paraNode);
                InitNode initNode = new InitNode(1, paraNode.getName(), paraNode.getType(), paraNode.getStartLine());
                initNodes.add(initNode);
            }
        }
        this.addChildren(paraNodes, cu);
        this.addAllParameters(paraNodes);
    }

    //===================== By Huyen: parse statement=============================

    /**
     * parse and get information of statement
     *
     * @param statements
     */
    public List<StatementNode> parseStatements(int level, List statements) {
        List<StatementNode> statementNodeList = new ArrayList<>();
        if (statements != null) {
            for (Object obj : statements) {
                int line = ASTHelper.getLine((ASTNode) obj, cu);
                StatementNode statementNode = null;
                if (obj instanceof Statement) {
                    statementNode = parseStatement(obj, level, line);
                } else if (obj instanceof Block) {
                    List<StatementNode> statementNodes = parseStatements(level + 1, ((Block) obj).statements());
                    statementNodeList.addAll(statementNodes);
                }
                if (statementNode != null) {
                    this.statementNodes.add(statementNode);
                    statementNodeList.add(statementNode);
                }
            }
        }
        return statementNodeList;
    }

    private StatementNode parseStatement(Object obj, int level, int line) {
        StatementNode statementNode = null;
        final int typeNode = ((ASTNode) obj).getNodeType();
        if (obj instanceof ASTNode) {
            line = ASTHelper.getLine((ASTNode) obj, cu);
        }
        if (Checker.isBreakStatement(typeNode)) {
            // BreakStatement
        } else if (Checker.isConstructorInvocation(typeNode)) {
            // ConstructorInvocation
            List<StatementNode> args = parseObjects(((ConstructorInvocation) obj).arguments(), line);
            setArgumentInstance(args);
//            ConstructorInvocationNode construct = new ConstructorInvocationNode(this.getName(), args, (ASTNode) obj, line);

            MethodCalledNode methodCalledNode = new MethodCalledNode(
                    "this", args, (ASTNode) obj, line, classNode.getQualifiedName());
            methodCalledNode.setEndPostion(methodCalledNode.getEndPostion() - 1);
            MethodInvocationStmNode methodInvocationStmNode = new MethodInvocationStmNode
                    ((ASTNode) obj, line, classNode.getQualifiedName());
            methodInvocationStmNode.setChild(methodCalledNode, this);
            methodInvocationStmNode.setEndPostion(methodInvocationStmNode.getEndPostion()-1);
            statementNode = methodInvocationStmNode;
        } else if (Checker.isContinueStatement(typeNode)) {
            // ContinueStatement
        } else if (Checker.isExpressionStatement(typeNode)) {
            // ExpressionStatement
            statementNode = parseExpression(((ExpressionStatement) obj).getExpression(), line);
        } else if (Checker.isReturnStatement(typeNode)) {
            // ReturnStatement
            statementNode = parseExpression(((ReturnStatement) obj).getExpression(), line);

            if (statementNode instanceof Token) {
                (statementNode).setType(this.getReturnType());
            }
            if (statementNode != null) {
                statementNode.setNodeInstance(NodeInstance.RETURN);
            }
        } else if (Checker.isSuperConstructorInvocation(typeNode)) {
            // SuperConstructorInvocation
            //Not used
            MethodInvocationStmNode methodInvocationStmNode = new MethodInvocationStmNode
                    ((ASTNode) obj, line, classNode.getQualifiedName());
            methodInvocationStmNode.fullNameParent = ((ClassNode) this.getParent()).qualifiedName;

            List<StatementNode> args = parseObjects(((SuperConstructorInvocation) obj).arguments(), line);
            setArgumentInstance(args);
            MethodCalledNode methodCalledNode = new MethodCalledNode(
                    "super", args, (ASTNode) obj, line, classNode.getQualifiedName());
            methodCalledNode.setEndPostion(methodCalledNode.getEndPostion() - 1);
            methodInvocationStmNode.setChild(methodCalledNode, this);
            methodInvocationStmNode.setEndPostion(methodInvocationStmNode.getEndPostion() - 1);
            statementNode = methodInvocationStmNode;
        } else if (Checker.isSwitchCase(typeNode)) {
            // SwitchCase
            //not use
        } else if (Checker.isThrowStatement(typeNode)) {
            // ThrowStatement
            //Not used
        } else if (Checker.isTypeDeclarationStatement(typeNode)) {
            // TypeDeclarationStatement
            // parsed - da parse classes
//            logger.error("Chua xu ly isTypeDeclarationStatement");
        } else if (Checker.isIfStatement(typeNode)) {
            //isIfStatement
            statementNode = parseIfStatementInfo((IfStatement) obj, level, line);
        } else if (Checker.isVariableDeclarationStatement(typeNode)) {
            // VariableDeclarationStatement
            VariableDeclarationStatement variable = (VariableDeclarationStatement) obj;
            List<StatementNode> statementNodes = parseVariableDeclarationInfo(level, variable, ASTHelper.getLine((ASTNode) obj, cu));
            this.statementNodes.addAll(statementNodes);
        } else if (Checker.isTryStatement(typeNode)) {
            //isTryStatement
            parseTryInfo((TryStatement) obj, level, line);
        } else if (Checker.isEnhancedForStatement(typeNode)) {
            //isEnhancedForStatement
            parseEnhancedForInfo((EnhancedForStatement) obj, line, level);
        } else if (Checker.isSwitchStatement(typeNode)) {
            //isSwitchStatement
            parseSwitchStatementInfo((SwitchStatement) obj, level);
        } else if (Checker.isForStatement(typeNode)) {
            //isForStatement
            parseForInfo((ForStatement) obj, line, level);
        } else if (Checker.isWhileStatement(typeNode)) {
            //isWhileStatement
            if (((WhileStatement) obj).getBody() instanceof Block) {
                parseStatements(level, ((Block) ((WhileStatement) obj).getBody()).statements());
            } else if (((WhileStatement) obj).getBody() instanceof Statement) {
                statementNode = parseStatement(((WhileStatement) obj).getBody(), level, line);
            } else {
                logger.error("Chua xu ly");
            }
        } else if (Checker.isDoStatement(typeNode)) {
            //isDoStatement
            // not use expression do
            if (((DoStatement) obj).getBody() instanceof Block) {
                Block block = (Block) ((DoStatement) obj).getBody();
                parseStatements(level + 1, block.statements());
            } else {
                Statement stm = ((DoStatement) obj).getBody();
                statementNode = parseStatement(stm, level + 1, line);
            }
        } else if (Checker.isSynchronizedStatement(typeNode)) {
            //NOT parse expression
            if (((SynchronizedStatement) obj).getBody() instanceof Block) {
                Block block = ((SynchronizedStatement) obj).getBody();
                parseStatements(level, block.statements());
            } else {
                statementNode = parseStatement(((SynchronizedStatement) obj).getBody(), level, line);
            }
        } else if (Checker.isLabeledStatement(typeNode)) {
            if (((LabeledStatement) obj).getBody() instanceof Block) {
                parseStatements(level, (((Block) ((LabeledStatement) obj).getBody()).statements()));
            } else if (((LabeledStatement) obj).getBody() instanceof Statement) {
                statementNode = parseStatement(((LabeledStatement) obj).getBody(), level, line);
            } else {
                logger.error("Chua xu ly ((LabeledStatement) obj).getBody() != Statement");
            }
        } else if (Checker.isBlock(typeNode)) {
            parseStatements(level + 1, ((Block) obj).statements());
        } else {
            //ELSE
            if (!(obj instanceof ThrowStatement) && !(obj instanceof EmptyStatement)) {
                logger.error("Chưa xử lý:parseStatements `" + obj.toString() + "`");
            }
        }
        return statementNode;
    }

    /**
     * parse Argurement, if (argurement instanceof MethodInvocation), need to countinue parse
     * and save in List<Object> is list argurements of methodInvocation.
     *
     * @param objects
     * @param line
     * @return
     */
    private List<StatementNode> parseObjects(List objects, int line) {
        List<StatementNode> stmNodes = new ArrayList<>();
        if (objects.size() > 0) {
            for (Object obj : objects) {
                StatementNode statementNode = null;
                if (obj instanceof Expression) {
                    statementNode = parseExpression((Expression) obj, line);
                } else {
                    logger.error("Chua xu ly");
                }
                if (statementNode == null) {
                    statementNode = new UndefinedNode(obj.toString(), (ASTNode) obj,
                            line, classNode.getQualifiedName());
                }
                stmNodes.add(statementNode);
            }
        }
        return stmNodes;
    }

    private void parseTryInfo(TryStatement tryStm, int level, int line) {
        if (tryStm.getBody() != null) {
            if (tryStm.getBody() instanceof Block) {
                List statements = ((Block) tryStm.getBody()).statements();
                parseStatements(level + 1, statements);
                List<CatchClause> catchClauses = tryStm.catchClauses();
                for (CatchClause catchCl : catchClauses) {
                    List statementss = catchCl.getBody().statements();
                    parseStatements(level + 1, statementss);
                }
            } else {
                logger.error("Chưa xử lý:parseTryInfo ");
            }
        }
    }

    private void parseSwitchStatementInfo(SwitchStatement switchStatement, int level) {
        List statements = switchStatement.statements();
        parseStatements(level + 1, statements);
//        for (Object stm : statements) {
//            Statement stmConvert = (Statement) stm;
//            int line.txt = cu.getLineNumber(stmConvert.getStartPosition());
//            if (stm instanceof ReturnStatement) {
//                StatementNode statementNode = parseExpression(((ReturnStatement) stm).getExpression(), line.txt);
//                if (statementNode instanceof Token) {
//                    ((Token) statementNode).setType(this.getReturnType());
//                }
//            }
//        }
    }

    private void parseEnhancedForInfo(EnhancedForStatement stm, int line, int level) {
        if (stm.getParameter() != null) {
//            String parseType = ASTHelper.getFullyQualifiedName(className, ), cu);
//            String type = ReflectionHelper.findFieldType(,
//                    );
            String type = ASTHelper.getFieldType(((ClassNode) this.getParent()).getQualifiedName(),
                    this.classNode, stm.getParameter().getName().getIdentifier(), stm.getParameter().getType(),
                    cu);
            InitNode initNode = new InitNode(level + 1, stm.getParameter().getName().getIdentifier(),
                    type, line);
            this.initNodes.add(initNode);
        } else {
            logger.error("Chưa xử lý:parseEnhancedForInfo: " + stm.getParameter());
        }
        if (stm.getBody() != null) {
            if (stm.getBody() instanceof Block) {
                List statements = ((Block) stm.getBody()).statements();
                parseStatements(level + 1, statements);
            } else {
                StatementNode statementNode = parseStatement(stm.getBody(), level + 1, line);
                if (statementNode != null) {
                    this.statementNodes.add(statementNode);
                }
//                logger.error("Chưa xử lý:parseEnhancedForInfo ");
            }
        }
    }

    private void parseForInfo(ForStatement stm, int line, int level) {
        List initializers = stm.initializers();
        // step1 parse inits;
        for (Object objInit : initializers) {
            if (objInit instanceof VariableDeclarationStatement) {
                parseVariableDeclarationInfo(level + 1, (VariableDeclarationStatement) objInit, line);
            } else if (objInit instanceof VariableDeclarationExpression) {
                parseVariableDeclarationInfo(level + 1, (VariableDeclarationExpression) objInit, line);
            } else if (objInit instanceof Assignment) {
                StatementNode statementNode = parseAssignmentStm((Expression) objInit, line);
                if (statementNode != null) {
                    this.statementNodes.add(statementNode);
                }
            } else if (objInit instanceof PostfixExpression) {
                //not use
            } else {
//                logger.info("Chua xy ly:parseForInfo");
            }
        }
        //TODO: step2: need to parse "optionalConditionExpression (i <n ) of forStatement
        //step 3: parse Block;
        if (stm.getBody() != null) {
            if (stm.getBody() instanceof Block) {
                List statements = ((Block) stm.getBody()).statements();
                parseStatements(level + 1, statements);
            } else {
                StatementNode statementNode = parseStatement(stm.getBody(), level + 1, line);
                if (statementNode != null) {
                    this.statementNodes.add(statementNode);
                }
//                logger.error("Chưa xử lý:parseEnhancedForInfo ");
            }
        }
    }

    private StatementNode parseIfStatementInfo(IfStatement ifStatement, int level, int line) {
        //DK
        StatementNode conditional = parseExpression(ifStatement.getExpression(), line);
        IfStmNode ifStmNode = new IfStmNode(conditional, ifStatement.getExpression(), line);

        if (ifStatement.getThenStatement() != null) {
            if (ifStatement.getThenStatement() instanceof Block) {
                List statements = ((Block) ifStatement.getThenStatement()).statements();
                parseStatements(level + 1, statements);
            } else if (ifStatement.getThenStatement() instanceof Statement) {
                StatementNode statementNode = parseStatement(ifStatement.getThenStatement(), level + 1, line);
                if (statementNode != null) {
                    this.statementNodes.add(statementNode);
//                    ifStmNode.setChild(statementNode, this);
                }
            } else {
                logger.error("Chưa xử lý:parseEnhancedForInfo ");
            }
        }
        if (ifStatement.getElseStatement() != null) {
            if (ifStatement.getElseStatement() instanceof Block) {
                List statements = ((Block) ifStatement.getElseStatement()).statements();
                parseStatements(level + 1, statements);
            } else if (ifStatement.getElseStatement() instanceof Statement) {
                StatementNode statementNode = parseStatement(ifStatement.getElseStatement(), level + 1, line);
                if (statementNode != null) {
                    this.statementNodes.add(statementNode);
                    //ifStmNode.setChild(statementNode, this);
                }
            } else {
                logger.error("Chưa xử lý:parseEnhancedForInfo ");
            }
        }
        return ifStmNode;
    }


    private List<StatementNode> parseVariableDeclarationInfo(int level, VariableDeclarationStatement variableDeclarationStatement, int line) {
        List<VariableDeclarationFragment> astNodes = variableDeclarationStatement.fragments();
        List<StatementNode> statementNodes = new ArrayList<>();
//        String type = ASTHelper.getFullyQualifiedName(className, variableDeclarationStatement.getType(), cu);
        String type = null;

        for (VariableDeclarationFragment astNode : astNodes) {
            type = ASTHelper.getFieldType(((ClassNode) this.getParent()).getQualifiedName(),
                    this.classNode, astNode.getName().getIdentifier(), variableDeclarationStatement.getType(), cu);
            InitNode initNode = new InitNode(level, astNode.getName().getIdentifier(),
                    type, line);
            initNodes.add(initNode);

            BaseVariableNode baseVariableNode = new BaseVariableNode(astNode.getName(),
                    initNode.getType(), line, classNode.getQualifiedName());
            StatementNode statementNode = parseExpression(astNode.getInitializer(), line);
            if (statementNode != null) {
                if (statementNode.getType() == null) {
                    statementNode.setType(baseVariableNode.getType());
                }
            }
            baseVariableNode.setNodeInstance(NodeInstance.LEFT_VAR_DECLARATION);
            if (statementNode != null) {
                statementNode.setNodeInstance(NodeInstance.RIGHT_VAR_DECLARATION);
            }
            ExpressionNode expression = new ExpressionNode(baseVariableNode,
                    statementNode, type, astNode, line, classNode.getQualifiedName());
            statementNodes.add(expression);
        }
        return statementNodes;
    }

    private void parseVariableDeclarationInfo(int level, VariableDeclarationExpression variableDeclarationStatement, int line) {
        List<VariableDeclarationFragment> astNodes = variableDeclarationStatement.fragments();
        for (VariableDeclarationFragment astNode : astNodes) {
            String type = ASTHelper.getFieldType(((ClassNode) this.getParent()).getQualifiedName(), this.classNode,
                    astNode.getName().getIdentifier(), variableDeclarationStatement.getType(), cu);
            InitNode initNode = new InitNode(level, astNode.getName().getIdentifier(), type, line);
            initNodes.add(initNode);
        }
    }


    private AssignmentNode parseAssignmentStm(Expression expression, int line) {

        Assignment asm = (Assignment) expression;
        StatementNode leftNode = parseExpression(asm.getLeftHandSide(), line);
        if (leftNode != null) {
            leftNode.setNodeInstance(NodeInstance.LEFT_ASSIGNMENT);
        }
        StatementNode rightNode = parseExpression(asm.getRightHandSide(), line);
        if (rightNode != null) {
            rightNode.setNodeInstance(NodeInstance.RIGHT_ASSIGNMENT);
            setType(leftNode, rightNode);
        }
        return new AssignmentNode(asm.getOperator().toString(),
                leftNode, rightNode, line, asm, classNode.getQualifiedName());
    }

    private void setType(StatementNode leftNode, StatementNode rightNode) {
//        if (leftNode instanceof Token && rightNode instanceof Token) {
        if (rightNode.getType() == null) {
            if (leftNode != null) {
                rightNode.setType(leftNode.getType());
            }
        }
//        }
    }


    private StatementNode parseExpression(Expression expression, int line) {
        StatementNode statementNode = null;
        if (expression != null) {
            line = ASTHelper.getLine(expression, cu);
        }
        if (expression != null) {
            final int typeNode = expression.getNodeType();
            if (Checker.isMethodInvocation(typeNode)
                    || Checker.isSuperMethodInvocation(typeNode)) {
                MethodInvocationStmNode methodInvocationStmNode =
                        new MethodInvocationStmNode(expression, line, classNode.getQualifiedName());
                methodInvocationStmNode.fullNameParent = ((ClassNode) this.getParent()).qualifiedName;
                parseInvoStm(methodInvocationStmNode, expression, line);
                methodInvocationStmNode.setChildren(
                        ((ClassNode) this.getParent()).getQualifiedName(),
                        cu, this);
                statementNode = methodInvocationStmNode;
            } else {
                statementNode = parseExpressionWithoutMethodInvo(expression, line);
            }
        }
        return statementNode;
    }

    private StatementNode parseExpressionWithoutMethodInvo(Expression expression, int line) {
        StatementNode statementNode = null;
        if (expression != null) {
            final int typeNode = expression.getNodeType();
            if (Checker.isArrayAccess(typeNode)) {
                // ArrayAccess
                StatementNode arrayExp = parseExpression(((ArrayAccess) expression).getArray(), line);
                StatementNode indexExp = parseExpression(((ArrayAccess) expression).getIndex(), line);
                ArrayAccessNode arrayAccessNode = new ArrayAccessNode(arrayExp, indexExp,
                        expression, line, classNode.getQualifiedName());
                statementNode = arrayAccessNode;
            } else if (Checker.isArrayCreation(typeNode)) {
                // ArrayCreation
                statementNode = new ArrayCreationNode(expression, line,
                        classNode.getQualifiedName());
                if (((ArrayCreation) expression).getInitializer() != null) {
                    StatementNode child = parseExpression(((ArrayCreation) expression).getInitializer(), line);
                    statementNode.setChild(child, this);
                }
            } else if (Checker.isArrayInitializer(typeNode)) {
                // ArrayInitializer
                statementNode = new ArrayInitializerNode(expression, line, classNode.getQualifiedName());
            } else if (Checker.isAssignment(typeNode)) {
                // Assignment
                statementNode = parseAssignmentStm(expression, line);
            } else if (Checker.isCastExpression(typeNode)) {
                // CastExpression
                statementNode = parseExpression(((CastExpression) expression).getExpression(), line);
                String type = ASTHelper.getFullyQualifiedName(this.classNode, ((CastExpression) expression).getType(), cu);
                if (statementNode instanceof Token) {
                    statementNode.setType(type);
                    statementNode.setCast("(" + ((CastExpression) expression).getType() + ")");
//                    statementNode.setCast(expression.);
                    statementNode.setStartPostion(expression.getStartPosition());
//                    statementNode.setStatementString(expression.toString());
                }
            } else if (Checker.isClassInstanceCreation(typeNode)) {
                // ClassInstanceCreation
                String type = ASTHelper.getFullyQualifiedName(this.classNode, ((ClassInstanceCreation) expression).getType(), cu);
                List<StatementNode> args = parseObjects(((ClassInstanceCreation) expression).arguments(), line);
                setArgumentInstance(args);
                statementNode = new ClassInstanceCreationNode(type, args,
                        (ASTNode) expression, line, classNode.getQualifiedName());
            } else if (Checker.isConditionalExpression(typeNode)) {
                // ConditionalExpression
                ConditionalExpression conditionalExpression = (ConditionalExpression) expression;
                statementNode = parseConditionalExpression(conditionalExpression, line);
            } else if (Checker.isConstructorInvocation(typeNode)) {
                // ConstructorInvocation
                //not use
                logger.info("isConstructorInvocation");
            } else if (Checker.isFieldAccess(typeNode)) {
                // FieldAccess
                FieldAccess fieldAccess = (FieldAccess) expression;
                StatementNode firstSide = parseExpression(fieldAccess.getExpression(), line);
                StatementNode lastSide = parseExpression(fieldAccess.getName(), line);
                if (firstSide != null) {
                    if (firstSide.getType() != null) {
                        String type = null;
                        //is != private
                        type = ReflectionHelper.findFieldType(firstSide.getType(),
                                fieldAccess.getName().getIdentifier(), this);
                        lastSide.setType(type);
                    }
                    if (firstSide instanceof MethodInvocationStmNode) {
                        ((MethodInvocationStmNode) firstSide).setChildType(lastSide, this);
                    } else {
                        firstSide.setChild(lastSide, this);
                        lastSide.setParent(firstSide);
                        if (firstSide instanceof BaseVariableNode && lastSide instanceof BaseVariableNode) {
                            List<BaseVariableNode> baseVars = new ArrayList<>();
                            baseVars.add((BaseVariableNode) firstSide);
                            firstSide.clearChildren();
                            baseVars.add((BaseVariableNode) lastSide);
                            statementNode = new QualifiedNameNode(baseVars, expression, line, classNode.getQualifiedName(), this);

                        } else {
                            statementNode = firstSide;
                        }
                    }
                } else {
                    statementNode = lastSide;
                }
            } else if (Checker.isThisExpression(typeNode)) {
                if (((ThisExpression) expression).getQualifier() != null) {
                    String initNode = ((ClassNode) this.getParent())
                            .findTypeVar(((ThisExpression) expression)
                                    .getQualifier().getFullyQualifiedName(), null);
                    if (initNode != null) {
                        statementNode = new BaseVariableNode(
                                ((ThisExpression) expression).getQualifier(),
                                ((ThisExpression) expression).getQualifier().getFullyQualifiedName(),
                                initNode, cu, classNode.getQualifiedName());
                    } else {
//                    initNodes.add()
                        //in super Class => ??????
                        StatementNode stm = parseExpression(((ThisExpression) expression).getQualifier(), line);
                        if (stm != null) {
                            StatementNode child = parseExpression(((ThisExpression) expression).getQualifier(), line);
                            stm.setChild(child, this);
                            statementNode = stm;
                        }
                    }
                } else {
                    BaseVariableNode baseVariableNode = new BaseVariableNode(
                            "this", ((ClassNode) this.getParent()).getQualifiedName(),
                            line, classNode.getQualifiedName());
                    baseVariableNode.setType(((ClassNode) this.getParent()).getQualifiedName());
                    statementNode = baseVariableNode;
                }
            } else if (Checker.isInfixExpression(typeNode)) {
                // InfixExpression
                InfixExpression infixEx = (InfixExpression) expression;
                List<StatementNode> objects = parseObjects(infixEx.extendedOperands(), line);
                StatementNode left = parseExpression(infixEx.getLeftOperand(), line);
                StatementNode right = parseExpression(infixEx.getRightOperand(), line);
                statementNode = new InfixExpressionStmNode(infixEx.getOperator().toString(),
                        left, right, objects,line,  expression.toString(), infixEx,
                        classNode.getQualifiedName());
            } else if (Checker.isMethodInvocation(typeNode)) {
                // MethodInvocation
                //not use
                logger.info("isMethodInvocation");
            } else if (Checker.isParenthesizedExpression(typeNode)) {
                // ParenthesizedExpression
                statementNode = parseExpression(((ParenthesizedExpression) expression).getExpression(), line);
                if (statementNode != null) {
                    statementNode.setLparen("(");
                    statementNode.setRparen(")");
                    Position pos = ASTHelper.getPosition(expression);
                    statementNode.setStartPostion(pos.getStartPos());
                    statementNode.setEndPostion(pos.getEndPos());
                }
            } else if (Checker.isPostfixExpression(typeNode)) {
                // PostfixExpression
                StatementNode leftNode = parseExpression(((PostfixExpression) expression).getOperand(), line);
                if (leftNode != null) {
                    statementNode = new InfixExpressionStmNode(((PostfixExpression) expression).getOperator().toString(),
                            leftNode, (StatementNode) null, null, line,
                            expression.toString(), expression, classNode.getQualifiedName());
                } else {
                    statementNode = new InfixExpressionStmNode(((PostfixExpression) expression).getOperator().toString(),
                            ((PostfixExpression) expression).getOperand(), (StatementNode) null, null, line,
                            expression.toString(), expression, classNode.getQualifiedName());
                }
//                logger.info("isPostfixExpression");
            } else if (Checker.isPrefixExpression(typeNode)) {
                // PrefixExpression
                PrefixExpression preFix = (PrefixExpression) expression;
                StatementNode exp = parseExpression(((PrefixExpression) expression).getOperand(), line);
                if (exp != null) {
                    statementNode = new InfixExpressionStmNode(preFix.getOperator().toString(), (StatementNode) null,
                            exp, null, line, preFix.toString(), expression, classNode.getQualifiedName());
                } else {
                    statementNode = new InfixExpressionStmNode(preFix.getOperator().toString(), null,
                            ((PrefixExpression) expression).getOperand(), null, line, preFix.toString(), expression, classNode.getQualifiedName());
                }
            } else if (Checker.isQualifiedName(typeNode)) {
                // QualifiedName
                List<BaseVariableNode> baseVariableNodes = parseQualifiedNameNode((QualifiedName) expression, line);
                Collections.reverse(baseVariableNodes);
//                String objectType = baseVariableNodes.get(0).getType();
//                for (int i = 1; i < baseVariableNodes.size(); i++) {
//                    BaseVariableNode baseVariableNode = baseVariableNodes.get(i);
//                    String nameType = ReflectionHelper.findFieldType(objectType,
//                            baseVariableNode.getKeyVar());
//                    if (nameType == null) {
//
//                    } else {
//                        baseVariableNode.setType(nameType);
//                    }
//                    statementNode = new QualifiedNameNode(baseVariableNodes, expression, line);
//                }
                statementNode = new QualifiedNameNode(baseVariableNodes, expression, line
                        , classNode.getQualifiedName(), this);
            } else if (Checker.isSuperFieldAccess(typeNode)) {
                // SuperFieldAccess
                logger.info("isSuperFieldAccess");
            } else if (Checker.isSuperMethodInvocation(typeNode)) {
                // SuperMethodInvocation
                //Not use
                logger.info("isSuperMethodInvocation");
            } else if (Checker.isVariableDeclarationExpression(typeNode)) {
                // VariableDeclarationExpression
                logger.info("isVariableDeclarationExpression");
            } else if (Checker.isVariableDeclarationFragment(typeNode)) {
                // VariableDeclarationFragment FIXME: this node is not an expression node.
                logger.info("isVariableDeclarationFragment");
            } else if (Checker.isInstanceofExpression(typeNode)) {
                // InstanceofExpression
                // Not use
//                logger.info("isInstanceofExpression");
            } else if (Checker.isLambdaExpression(typeNode)) {
                // LambdaExpression
//                logger.info("isLambdaExpression");
                LambdaExpression lambdaExpression = (LambdaExpression) expression;
                if (lambdaExpression.getBody() instanceof Block) {
                    parseStatements(-1, ((Block) lambdaExpression.getBody()).statements());
                } else if (lambdaExpression.getBody() instanceof Statement) {
                    statementNode = parseStatement(lambdaExpression.getBody(), -1, line);
                }
            } else if (Checker.isSimpleName(typeNode)) {
                //isSimpleName
                InitNode initNode = findTypeVar(((SimpleName) expression).getIdentifier(), line);
                if (initNode != null) {
                    statementNode = new BaseVariableNode((SimpleName) expression,
                            initNode.getType(), line, classNode.getQualifiedName());
                } else {
                    String type = null;
                    type = ReflectionHelper.findFieldType(((ClassNode) this.getParent()).getQualifiedName(),
                            ((SimpleName) expression).getIdentifier(), this);
                    if (type == null) {
                        type = ASTHelper.getFullyQualifiedTypeName(this.classNode,
                                ((SimpleName) expression).getIdentifier(), cu);
                    }

                    statementNode = new BaseVariableNode((SimpleName) expression,
                            type, line, classNode.getQualifiedName());
                }
            } else if (Checker.isStringLiteral(typeNode)) {
                //isStringLiteral
                String value = ((StringLiteral) expression).getEscapedValue();
                statementNode = new StringNode(line, null,
                        value, expression.toString(), expression, classNode.getQualifiedName());
            } else if (Checker.isCharacterLiteral(typeNode)) {
                //isCharacterLiteral
                String value = ((CharacterLiteral) expression).getEscapedValue();
                statementNode = new StringNode(line, null, value,
                        expression.toString(), expression, classNode.getQualifiedName());
            } else if (Checker.isNumberLiteral(typeNode)) {
                //isNumberLiteral
                statementNode = new NumbericNode(expression, expression, classNode.getQualifiedName());
            } else if (Checker.isBooleanLiteral(typeNode)) {
                //isBooleanLiteral
                statementNode = new BooleanNode(((BooleanLiteral) expression).booleanValue(),
                        expression, expression.toString(), line, classNode.getQualifiedName());

            } else {
                //ELSE
                statementNode = new UndefinedNode(expression.getClass().getName(),
                        (ASTNode) expression, line, classNode.getQualifiedName());
                if (!Checker.isThisExpression(expression.getNodeType())
                        && !Checker.isNullLiteral(expression.getNodeType())
                        && !Checker.isTypeLiteral(typeNode)) {
                    logger.error("Chưa xử lý:parseExpressionWithoutMethodInvo " + expression.toString());
                }

            }
        }
        return statementNode;
    }

    private void setArgumentInstance(List<StatementNode> args) {
        for (StatementNode statementNode : args) {
            statementNode.setNodeInstance(NodeInstance.ARGUMENT);
            statementNode.setParamSize(args.size());
        }
    }

    private List<BaseVariableNode> parseQualifiedNameNode(Name node, int line) {
        List<BaseVariableNode> baseVariableNodes = new ArrayList<>();
        if (node instanceof QualifiedName) {

            BaseVariableNode name = new BaseVariableNode(((QualifiedName) node).getName(),
                    null, line, classNode.getQualifiedName());
            baseVariableNodes.add(name);
            if (((QualifiedName) node).getQualifier() instanceof SimpleName) {
                InitNode initNode = findTypeVar(((QualifiedName) node).getQualifier().getFullyQualifiedName(), line);
                String type;
                if (initNode == null) {
                    type = ASTHelper.getFieldType(((ClassNode) this.getParent()).getQualifiedName(),
                            this.classNode, ((QualifiedName) node).getQualifier().getFullyQualifiedName(), null, cu);
                } else {
                    type = initNode.getType();
                }
                BaseVariableNode qualifier = new BaseVariableNode(((QualifiedName) node).getQualifier(),
                        type, line, classNode.getQualifiedName());
                baseVariableNodes.add(qualifier);
            } else if (((QualifiedName) node).getQualifier() instanceof QualifiedName) {
                List<BaseVariableNode> baseVariableNodeList = parseQualifiedNameNode(((QualifiedName) node).getQualifier(), line);
                baseVariableNodes.addAll(baseVariableNodeList);
            }
        }
        return baseVariableNodes;
    }

    private StatementNode parseConditionalExpression(ConditionalExpression conditionalExpression, int line) {
        StatementNode inFix = parseExpression(conditionalExpression.getExpression(), line);
        StatementNode then = parseExpression(conditionalExpression.getThenExpression(), line);
        StatementNode elseNode = parseExpression(conditionalExpression.getElseExpression(), line);
        StatementNode condition = null;
        if (inFix instanceof InfixExpressionStmNode) {
            condition = inFix;
        } else {
            condition = new InfixExpressionStmNode("", (StatementNode) null, inFix, null, line,
                    conditionalExpression.getExpression().toString(),
                    conditionalExpression.getExpression(), classNode.getQualifiedName());
        }
        IfStmNode ifStmNode = new IfStmNode(condition, conditionalExpression, line);
        //TODO: noticed a line.txt has multi values
        if (then != null) {
            ifStmNode.setChild(then, this);
//            this.statementNodes.add(then);
        }
        if (elseNode != null) {
            ifStmNode.setChild(elseNode, this);
//            this.statementNodes.add(elseNode);
        }
        return ifStmNode;
    }

    public void parseInvoStm(MethodInvocationStmNode node, Expression expression, int line) {
        if (expression != null) {
            final int typeNode = expression.getNodeType();
            if (Checker.isMethodInvocation(typeNode)
                    || Checker.isSuperMethodInvocation(typeNode)) {
                List<StatementNode> argTypes = new ArrayList<>();
                if (Checker.isMethodInvocation(typeNode)) {
                    //in
                    parseInvoStm(node, ((MethodInvocation) expression).getExpression(), line);

                    argTypes = parseObjects(((MethodInvocation) expression).arguments(), line);
                    setArgumentInstance(argTypes);
                    int endPos = expression.getStartPosition() + expression.getLength();
                    MethodCalledNode methodCalledNode = new MethodCalledNode(
                            ((MethodInvocation) expression).getName().getIdentifier(),
                            argTypes, ((MethodInvocation) expression).getName(), line
                            , classNode.getQualifiedName());
                    methodCalledNode.setEndPostion(endPos);
                    node.addNode(methodCalledNode);
                } else if (Checker.isSuperMethodInvocation(typeNode)) {
                    argTypes = parseObjects(((SuperMethodInvocation) expression).arguments(), line);
                    setArgumentInstance(argTypes);
                    MethodCalledNode methodCalledNode = new MethodCalledNode(
                            ((SuperMethodInvocation) expression).getName().getIdentifier(),
                            argTypes, ((SuperMethodInvocation) expression).getName(),
                            line, classNode.getQualifiedName());
                    node.addNode(methodCalledNode);

//                    BaseVariableNode baseVariableNode = new BaseVariableNode("super", expression, line);
//                    baseVariableNode.setType(((ClassNode) this.getParent()).parentClass);
//                    node.addNode(baseVariableNode);
                }
            } else {
                StatementNode statementNode = parseExpressionWithoutMethodInvo(expression, line);
                if (statementNode instanceof BaseVariableNode) {
                    if (statementNode.getChildren().size() == 0) {
                        node.addNode(statementNode);
                    } else {
                        //field node
                        node.addNode(statementNode);
                        node.addNode(statementNode.getChildren().get(0));
                        statementNode.getChildren().clear();
                    }
                } else if (statementNode instanceof MethodInvocationStmNode) {
//                    Collections.reverse(((MethodInvocationStmNode) statementNode).getNodes());
                    for (StatementNode stmNode : ((MethodInvocationStmNode) statementNode).getNodes()) {
                        if (stmNode != null) {
                            node.addNode(stmNode);
                            stmNode.getChildren().clear();
                        }
                    }
                } else {
                    node.addNode(statementNode);
                }

            }
        }

    }

    /**
     * find index of InitNode in initNodes (initNode has type var)
     *
     * @param varname
     * @param line
     * @return
     */
    public int findIndexTypeVar(String varname, int line) {
        for (int i = 0; i < initNodes.size(); i++) {
            InitNode initNode = initNodes.get(i);
            if (initNode.getVarname().equals(varname)) {
                if (line <= this.getEndLine() &&
                        line >= this.getStartLine()) {
                    return i;
                }
            }
        }
        return -1;
    }

    public InitNode findTypeVar(String varname, int line) {
        List<InitNode> arr = new ArrayList<>();
        for (InitNode init : initNodes) {
            if (init.getVarname() != null) {
                if (init.getVarname().equals(varname)) {
                    if (line <= this.getEndLine() &&
                            line >= this.getStartLine()) {
                        arr.add(init);
                    }
//                    else {
//                        logger.error("Chưa xử lý findTypeVar1");
//                    }
                }
            }
        }
        ClassNode classNode = (ClassNode) this.getParent();
        List<InitNode> initNodes = classNode.getInitNodes();
        for (InitNode init : initNodes) {
            if (init.getVarname().equals(varname)) {
                arr.add(init);
            }
        }
        Collections.sort(arr, new Comparator<InitNode>() {
            @Override
            public int compare(InitNode o1, InitNode o2) {
                return o2.getLine() - o1.getLine();
            }
        });
        if (arr.size() > 0) {
            return arr.get(0);
        }
//        String parent = classNode.getParentClass();

        return null;
    }

    public StatementNode findStatementByLine(int line) {
        for (StatementNode stm : statementNodes) {
            if (stm.getLine() == line) {
                return stm;
            }
        }
        return null;
    }

    public void printInfor() {
        System.out.println("Method name: " + name + "   Type: " + returnType +
//                "  Visibility: " + this.getVisibility() +
                " startline:" + this.getStartLine() + " ENDLINE: " + this.endLine);
    }
}
