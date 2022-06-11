package AST.parser;

import AST.node.ClassNode;
import AST.node.EnumNode;
import AST.stm.abst.StatementNode;
import AST.stm.token.ClassInstanceCreationNode;
import AST.stm.token.InfixExpressionStmNode;
import AST.stm.token.MethodCalledNode;
import AST.stm.token.Token;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaFileParser {

    public static List<ClassNode> parse(File file, String sourceCode) {

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(sourceCode.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);
        parser.setResolveBindings(true);

        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5); //or newer version
        parser.setCompilerOptions(options);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        List<ClassNode> classes = new ArrayList<>();
        cu.accept(new ASTVisitor() {
            public boolean visit(TypeDeclaration node) {
                ClassNode classNode = new ClassNode();
                if (node != null) {
                    classNode.file = file;
                    classNode.parseClasses(node, cu);
                    classes.add(classNode);
//                    return false;
                }
                return true;
            }

            public boolean visit(EnumDeclaration node) {
                EnumNode enumNode = Convert.convertToEnumNode(null, node, cu);
                if (enumNode != null) {
                    classes.add(enumNode);
//                    return false;
                }
                return true;
            }
        });
//        System.out.println("END");
        return classes;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        File file = new File("/home/huyenhuyen/ReRepair/src/main/java/AST/parser/ClassA.java");
//        String content = FileService.readFileToString(file.getAbsolutePath());
//        List<ClassNode> classNodes = JavaFileParser.parse(content);
//        FolderNode folderNode = ProjectParser.parse("/home/huyenhuyen/ReRepair/src/main/java/AST/parser");
        System.setProperty("user.dir", "/home/huyenhuyen/Desktop/benmarks/Closure_14/");
        System.setProperty("java.library.path",
                System.getProperty("java.library.path")
                        + System.getProperty("file.separator")
//                        + OSHelper.delimiter()
                        + "/home/huyenhuyen/Desktop/benmarks/Closure_14");
//        Runtime.getRuntime().loadLibrary("/home/huyenhuyen/Desktop/benmarks/Closure_14/lib/");
        Class<?> cls = Class.forName("java.util.AbstractCollection");
        Field[] fields = cls.getFields();
        Method[] methods = cls.getMethods();
//        Class aClass = Class.forName("com.google.javascript.rhino.Node");
//        if (aClass.isEnum()) {
//            for (Object obj : aClass.getEnumConstants())
//            System.out.println("Enum");
//        }
//        Class bClass = Class.forName("AST.parser.ClassA$ClassB");
//
//        Field[] fields1 = aClass.getFields();
//        Method[] methods1 = aClass.getMethods();

        File customElementsDir = new File("/home/huyenhuyen/Desktop/benmarks/Closure_14/build/classes");
        URL url = customElementsDir.toURI().toURL();
        File libFolder = new File("/home/huyenhuyen/ReRepair/lib");
        URL lib = libFolder.toURI().toURL();
        URL[] urls = new URL[] { url, lib};
        URLClassLoader loader = new URLClassLoader(urls);

        Class<?> loadClass = loader.loadClass("com.google.javascript.jscomp.RhinoErrorReporter");
        System.out.println(loadClass);


    }

//    static private Enum<?> getEnum(String enumFullName) {
//        @SuppressWarnings("unchecked")
//        final Class<Enum> cl = (Class<Enum>) Class.forName(enumClassName);
//        @SuppressWarnings("unchecked")
//        final Enum result = Enum.valueOf(cl, enumName);
//        return result;
//    }

    private static void parseStatmentNode(StatementNode statementNode, HashMap<Integer, List<StatementNode>> nullTypes) {
        if (statementNode instanceof Token) {
            if (!(statementNode instanceof InfixExpressionStmNode)) {
                Integer hash = ((Token) statementNode).getHashCode();
                if (statementNode.getType() == null) {
                    if (!nullTypes.containsKey(hash)) {
                        List<StatementNode> statementNodes = new ArrayList<>();
                        statementNodes.add(statementNode);
                        nullTypes.put(hash, statementNodes);
                    } else {
                        nullTypes.get(hash).add(statementNode);
                    }
                } else {
                    if (nullTypes.containsKey(hash)) {
                        for (StatementNode nullNode : nullTypes.get(hash)) {
                            nullNode.setType(statementNode.getType());
                        }
                        nullTypes.remove(hash);
                    }
                }
            }
        }
        if (statementNode instanceof MethodCalledNode) {
            for (StatementNode child : ((MethodCalledNode) statementNode).getArguments()) {
                parseStatmentNode(child, nullTypes);
            }
        } else if (statementNode instanceof ClassInstanceCreationNode) {
            for (StatementNode child : ((ClassInstanceCreationNode) statementNode).getArgs()) {
                parseStatmentNode(child, nullTypes);
            }
        }
        for (StatementNode child : statementNode.getChildren()) {
            parseStatmentNode(child, nullTypes);
        }
    }

}
