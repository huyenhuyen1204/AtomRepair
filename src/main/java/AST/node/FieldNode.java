package AST.node;

import AST.stm.nodetype.InitNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import util.ASTHelper;

import java.util.ArrayList;
import java.util.List;

public class FieldNode extends JavaNode {

    protected String type = "abc";
    protected String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FieldNode{" +
                "id=" + this.id +
//                "visibility=" + this.getVisibility() +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
//                ", isStatic=" + this.isStatic() +
//                ", isFinal=" + this.isFinal() +
                ", value='" + value + '\'' +
                '}';
    }

    public static List<FieldNode> setInforFromASTNode(FieldDeclaration node, ClassNode classNode, CompilationUnit cu, List<InitNode> initNodes) {
        List<FieldNode> fieldNodes = new ArrayList<>();
        for (int i = 0; i < node.fragments().size(); i++) {
            FieldNode fieldNode = new FieldNode();
//            fieldNode.setType(ASTHelper.getFullyQualifiedName(className, node.getType(), (CompilationUnit)node.getRoot()));
            fieldNode.setStartPosition(node.getStartPosition());
            fieldNode.setStartLine(cu.getLineNumber(node.getStartPosition()));
            //set ten cua thuoc tinh
            if (node.fragments().get(i) instanceof VariableDeclarationFragment) {
                VariableDeclarationFragment vdf = (VariableDeclarationFragment) node.fragments().get(i);
                fieldNode.setName(vdf.getName().getIdentifier());
                //setValue cho thuoc tinh
                Expression expression = vdf.getInitializer();
                if (expression != null) {
                    fieldNode.setValue(expression.toString());
                }
//                String field = ReflectionHelper.findFieldType(classNode.getQualifiedName(), fieldNode.getName());
//                field = ReflectionHelper.formatExpectedType(field);
//                String type = ASTHelper.getFullyQualifiedName(classNode, node.getType(), (CompilationUnit)node.getRoot());
                String type = ASTHelper.getFieldType(classNode.getQualifiedName(), classNode, fieldNode.getName(), node.getType(), cu);
                fieldNode.setType(type);
                //                if (type.contains(field)) {
//                    fieldNode.setType(type);
//                } else {
//                    fieldNode.setType(field);
//                }

            } else {
                //TODO can nem ra exception
                fieldNode.setName("not be VariableDeclarationFragment");
            }

//            //set modifier cho thuoc tinh
//            List visibilityList = node.modifiers();
//            if (visibilityList.size() == 0) fieldNode.setVisibility(DEFAULT_MODIFIER);
//            else {
//                for (Object o : visibilityList) {
//                    if (o instanceof Modifier) {
//                        Modifier m = (Modifier) o;
//                        if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.PUBLIC_KEYWORD.toFlagValue()) {
//                            fieldNode.setVisibility(PUBLIC_MODIFIER);
//                        } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.PRIVATE_KEYWORD.toFlagValue()) {
//                            fieldNode.setVisibility(PRIVATE_MODIFIER);
//                        } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.PROTECTED_KEYWORD.toFlagValue()) {
//                            fieldNode.setVisibility(PROTECTED_MODIFIER);
//                        } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.STATIC_KEYWORD.toFlagValue()) {
//                            fieldNode.setStatic(true);
//                        } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.FINAL_KEYWORD.toFlagValue()) {
//                            fieldNode.setFinal(true);
//                        } else {
//                            fieldNode.setVisibility(DEFAULT_MODIFIER);
//                        }
//                    }
//                }
//            }
            fieldNodes.add(fieldNode);
            InitNode initInClassNode = new InitNode(0, fieldNode.getName(), fieldNode.getType(),
                    fieldNode.getStartLine());
//
            initNodes.add(initInClassNode);
        }
        return fieldNodes;
//        //TODO chua xet cac truong hop cua type
//        //set kieu tra ve cua thuoc tinh
//        this.type = ASTHelper.getFullyQualifiedName(node.getType(), (CompilationUnit)node.getRoot());
//        this.setStartPosition(node.getStartPosition());

//        //set ten cua thuoc tinh
//        if (node.fragments().get(0) instanceof VariableDeclarationFragment) {
//            VariableDeclarationFragment vdf = (VariableDeclarationFragment) node.fragments().get(0);
//            this.name = vdf.getName().getIdentifier();
//
//            //setValue cho thuoc tinh
//            Expression expression = vdf.getInitializer();
//            if (expression != null) this.value = expression.toString();
//        } else {
//            //TODO can nem ra exception
//            this.name = "not be VariableDeclarationFragment";
//        }
//
//        //set modifier cho thuoc tinh
//        List visibilityList = node.modifiers();
//        if (visibilityList.size() == 0) this.setVisibility(DEFAULT_MODIFIER);
//        else {
//            for (Object o : visibilityList) {
//                if (o instanceof Modifier) {
//                    Modifier m = (Modifier) o;
//                    if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.PUBLIC_KEYWORD.toFlagValue()) {
//                        this.setVisibility(PUBLIC_MODIFIER);
//                    } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.PRIVATE_KEYWORD.toFlagValue()) {
//                        this.setVisibility(PRIVATE_MODIFIER);
//                    } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.PROTECTED_KEYWORD.toFlagValue()) {
//                        this.setVisibility(PROTECTED_MODIFIER);
//                    } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.STATIC_KEYWORD.toFlagValue()) {
//                        this.setStatic(true);
//                    } else if (m.getKeyword().toFlagValue() == Modifier.ModifierKeyword.FINAL_KEYWORD.toFlagValue()) {
//                        this.setFinal(true);
//                    } else {
//                        this.setVisibility(DEFAULT_MODIFIER);
//                    }
//                }
//            }
//        }


    }

    public void printInfor() {
        System.out.println("Property name: " + name + "   Type: " + type +
//                "  Visibility: " + this.getVisibility() +
                " StartLine: " + this.startLine + " Endline: " + this.endLine);
    }
}
