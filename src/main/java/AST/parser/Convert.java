package AST.parser;

import AST.node.*;
import AST.obj.Position;
import AST.stm.nodetype.InitNode;
import org.eclipse.jdt.core.dom.*;
import util.ASTHelper;

import java.util.ArrayList;
import java.util.List;

public class Convert {
    public static List<Node> parseFields(FieldDeclaration[] fields, ClassNode classNode, CompilationUnit cu, List<InitNode> initNodes) {
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            List<FieldNode> fieldNodes = FieldNode.setInforFromASTNode(fields[i], classNode, cu, initNodes);
            result.addAll(fieldNodes);
        }
        return result;
    }

    public static List<Node> parseMethods(ClassNode classNode, MethodDeclaration[] methods, CompilationUnit cu) {
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            MethodNode methodNode = new MethodNode();
            methodNode.setInforFromASTNode(classNode, methods[i], cu);
            result.add(methodNode);
        }
        return result;
    }


    public static List<Node> parseClasses(String className, TypeDeclaration[] innerClasses, CompilationUnit cu) {
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < innerClasses.length; i++) {
            ClassNode classNode = new ClassNode();
            //lay ten
            if (innerClasses[i].getName() != null) {
                if (innerClasses[i].getName().getIdentifier() != null) {
                    String name = innerClasses[i].getName().getIdentifier();
                    classNode.setName(name);
                }
            }
            classNode.setQualifiedName(ASTHelper.getQualifiedNameInnerClass(className, classNode.getName(), cu));
            classNode.parseClasses(innerClasses[i], cu);
            result.add(classNode);
        }
        return result;
    }

    public static EnumNode convertToEnumNode(String className, EnumDeclaration obj, CompilationUnit cu) {
        EnumNode enumNode = new EnumNode();
        String name = obj.getName().getIdentifier();
        Position position = ASTHelper.getPosition(obj);
        enumNode.setStartPosition(position.getStartPos());
        enumNode.setEndPosition(position.getEndPos());
        enumNode.setName(name);
        enumNode.setQualifiedName(ASTHelper.getQualifiedNameInnerClass(className, obj.getName().getIdentifier(), cu));
        for (Object enumConstantDeclaration : obj.enumConstants()) {
            if (enumConstantDeclaration instanceof EnumConstantDeclaration) {
                enumNode.addEnumConstant(((EnumConstantDeclaration) enumConstantDeclaration)
                        .getName().getIdentifier());
            }
        }
        return enumNode;
    }
}
