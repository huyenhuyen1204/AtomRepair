package AST.parser;//package AST.parser;
//
//import AST.node.ClassNode;
//import AST.node.FieldNode;
//import AST.stm.node.InfixExpressionStmNode;
//import AST.node.MethodNode;
//import AST.stm.abst.AssertStatement;
//import AST.stm.node.AssertEqualStmNode;
//import AST.stm.node.AssertTrueStmNode;
//import AST.obj.MethodTest;
//import common.TestType;
//import org.eclipse.jdt.core.dom.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import util.FileService;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Parser mytest, have to list all AssertEquals usted to
// * Now, I have 3 cases:
// * 1. Assert.assertEquals(message, expect, actual),
// * 2. Assert.assertEquals(double expected, double actual, double delta),
// * 3. Assert.assertEquals(expect, actual)
// */
//public class MyTestParser {
//    private static Logger logger = LoggerFactory.getLogger(MyTestParser.class);
//
//
//    public List<ClassNode> myTestParser(String pathToFile, TestType type) throws IOException {
//        List<MethodTest> methodTests = new ArrayList<>();
//        File file = new File(pathToFile);
//        String content = FileService.readFileToString(file.getAbsolutePath());
//
//        ASTParser parser = ASTParser.newParser(AST.JLS8);
//        parser.setSource(content.toCharArray());
//        parser.setKind(ASTParser.K_COMPILATION_UNIT);
//        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
//        List<ClassNode> classNodes = new ArrayList<>();
//        cu.accept(new ASTVisitor() {
//            public boolean visit(TypeDeclaration node) {
//                ClassNode classNode = new ClassNode();
//                if (node != null) {
//                    classNode.setInforFromASTNode(node, cu);
//                    classNodes.add(classNode);
//                    return false;
//                }
//                return true;
//            }
//        });
//
//        for (ClassNode classNode : classNodes) {
//            System.out.println(classNode.toString());
//            List<FieldNode> fieldNodes = classNode.getFieldList();
//            for (FieldNode fieldNode : fieldNodes) {
//                System.out.println(fieldNode.toString());
//            }
//            List<MethodNode> methodNodes = classNode.getMethodList();
//            for (MethodNode methodNode : methodNodes) {
//                MethodTest methodTest = new MethodTest(methodNode.getName(), methodNode);
//                parserAssertStatements(methodTest, methodNode.getStatements(), type, cu);
//                methodTests.add(methodTest);
////                System.out.println(methodNode.toString());
//            }
//            classNode.setMethodTests(methodTests);
//        }
//        return classNodes;
//    }
//
//    /**
//     * parser for annotation
//     *
//     * @param type
//     * @return
//     */
//    private void parserAssertStatements(MethodTest methodTest, List stms, TestType type, CompilationUnit cu) {
////        MethodTest methodTest = new MethodTest(methodNode.getName(), methodNode);
//        List statements = stms;
//        int line.txt = -1;
//
//        for (Object stm : statements) {
//            if (type.equals(TestType.ANNOTATION_TEST)) {
//                if (stm instanceof ExpressionStatement) {
//                    Expression expression = ((ExpressionStatement) stm).getExpression();
//                    line.txt = cu.getLineNumber(expression.getStartPosition());
//                    //Case 1:
//                    if (expression instanceof MethodInvocation) {
//                        if (((MethodInvocation) expression).getName().toString().equals("assertEquals")) {
//                            List arguments = ((MethodInvocation) expression).arguments();
//                            AssertStatement assertEqual = getAssertEquals(arguments, line.txt);
//
//                            if (assertEqual != null) {
//                                methodTest.addToAsserts(assertEqual);
//                            }
//
//                        } else if (((MethodInvocation) expression).getName().toString().equals("assertTrue")) {
//                            List args = ((MethodInvocation) expression).arguments();
//                            AssertStatement assertTrue = getAssertTrue(args, line.txt);
//                            if (assertTrue != null) {
//                                methodTest.addToAsserts(assertTrue);
//                            }
//
//                        }
//                    }
//                } else if (stm instanceof TryStatement) {
//                    List stmss = ((TryStatement) stm).getBody().statements();
//                    parserAssertStatements(methodTest, stmss, type, cu);
//                    List<CatchClause> catchClauses = ((TryStatement) stm).catchClauses();
//                    for (CatchClause catchCl : catchClauses) {
//                        List statementss = catchCl.getBody().statements();
//                        parserAssertStatements(methodTest, statementss, type, cu);
//                    }
//                } else {
//                    logger.info("else");
//                }
//            }
//        }
////        return methodTest;
//    }
//
//    /**
//     * Now, I have 3 cases:
//     * 1. Assert.assertEquals(message, expect, actual),
//     * 2. Assert.assertEquals(double expected, double actual, double delta),
//     * 3. Assert.assertEquals(expect, actual)
//     *
//     * @param arguments
//     * @return
//     */
//    private AssertStatement getAssertEquals(List arguments, int line.txt) {
//        AssertEqualStmNode assertEqualStmNode = null;
//        // 1.for Assert.assertEquals(message, expected, actual)
//        if (arguments.size() == 3) {
//            if (arguments.get(0) instanceof StringLiteral) {
//                Object numbersString =  parserExpected(arguments.get(1), line.txt);
//                assertEqualStmNode = new AssertEqualStmNode((StringLiteral) arguments.get(0),
//                        numbersString, arguments.get(2), line.txt);
//            } else {
//                //2. Assert.assertEquals(double expected, double actual, double delta),
//                if (arguments.get(0) instanceof NumberLiteral
//                        && arguments.get(2) instanceof NumberLiteral) {
//                    assertEqualStmNode = new AssertEqualStmNode(arguments.get(0), arguments.get(1),
//                            (NumberLiteral) arguments.get(2), line.txt);
//                }
//            }
//            //3.  for Assert.assertEquals(expected, actual)
//        } else if (arguments.size() == 2) {
//            Object expected = parserExpected(arguments.get(0), line.txt);
//            assertEqualStmNode = new AssertEqualStmNode(
//                    expected, arguments.get(1), line.txt);
//        }
//        if (assertEqualStmNode == null) {
//            logger.error("CAN'T PARSER: assertEquals(" + arguments.toString() + ")");
//        }
//        return assertEqualStmNode;
//    }
//
//    private Object parserExpected(Object obj, int line.txt) {
//        if (obj instanceof InfixExpression) {
//            InfixExpression infixEx = (InfixExpression) obj;
//            List<Object> objects = convertListToStm(infixEx.extendedOperands());
//            Object left = convertToStm(infixEx.getLeftOperand());
//            Object right = convertToStm(infixEx.getRightOperand());
//            InfixExpressionStmNode infixExpressionStmNode = new InfixExpressionStmNode(infixEx.getOperator().toString(),
//                    left, right, objects, line.txt, null, infixEx.toString() );
//            return infixExpressionStmNode;
//        } else if (obj instanceof StringLiteral){
//            return ((StringLiteral) obj).getLiteralValue();
////            return JavaLibraryHelper.convertStringToNumbers((String) obj);
//        } else {
//            return obj;
//        }
//    }
//
//    private Object convertToStm(Expression leftOperand) {
//        if (leftOperand instanceof StringLiteral) {
//            String content = ((StringLiteral) leftOperand).getEscapedValue();
////            content = JavaLibrary.removeFirstAndLastChars(content);
////            content = JavaLibraryHelper.convertStringToNumbers(content);
//            return content;
//        } else {
//            return leftOperand;
//        }
//    }
//
//    private List<Object> convertListToStm(List extendedOperands) {
//        List<Object> objects = new ArrayList<>();
//        for (Object obj : extendedOperands) {
//            if (obj instanceof StringLiteral) {
//                String string = ((StringLiteral) obj).getEscapedValue();
////                string = JavaLibraryHelper.convertStringToNumbers(string);
//                objects.add(string);
//            } else {
//                objects.add(obj);
//            }
//        }
//        return objects;
//    }
//
//    /**
//     * Now, I have 2 cases:
//     * 1. Assert.assertTrue(boolean condition),
//     * 2. Assert.assertTrue(message, boolean condition),
//     *
//     * @param args
//     * @return
//     */
//    private AssertStatement getAssertTrue(List args, int line.txt) {
//        AssertTrueStmNode assertTrueStmNode = null;
//        if (args.size() == 1) {
//            assertTrueStmNode = new AssertTrueStmNode(args.get(0), line.txt);
//        } else if (args.size() == 2) {
//            if (args.get(0) instanceof StringLiteral) {
//                assertTrueStmNode = new AssertTrueStmNode((StringLiteral) args.get(0), args.get(1), line.txt);
//            }
//        }
//        if (assertTrueStmNode == null) {
//            logger.error("CAN'T PARSER: assertTrue(" + args.toString() + ")");
//        }
//        return assertTrueStmNode;
//    }
//
//    /**
//     * parser and get info of statement
//     *
//     * @param statements
//     */
//    public void parserStatements(List statements) {
//        for (Object stm : statements) {
//            if (stm instanceof VariableDeclarationStatement) {
////                System.out.println(((VariableDeclarationStatement) stm).getType());
//            } else if (stm instanceof IfStatement) {
////                System.out.println(((IfStatement) stm).getExpression());
//            } else if (stm instanceof ExpressionStatement) {
//                if (((ExpressionStatement) stm).getExpression() instanceof MethodInvocation) {
////                    System.out.println(stm.toString());
//                } else if (((ExpressionStatement) stm).getExpression() instanceof Assignment) {
////                    System.out.println("ASSIGN ment");
////                    System.out.println(((Assignment) ((ExpressionStatement) stm).getExpression()).getLeftHandSide());
//                }
//            } else if (stm instanceof ReturnStatement) {
//
//            }
//        }
//    }
//
//    public static void main(String[] args) throws IOException {
//        MyTestParser myTestParser = new MyTestParser();
//        myTestParser.myTestParser("C:\\Users\\Dell\\Desktop\\APR_Test\\data_test\\83778\\MyTest.java",
//                TestType.ANNOTATION_TEST);
//
////        JavaFileParser javaFileParser
//    }
//}
