package AST.node;

import AST.obj.Position;
import AST.parser.Convert;
import AST.stm.abst.StatementNode;
import AST.stm.nodetype.InitNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ASTHelper;
import util.ReflectionHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassNode extends JavaNode {
    public static final Logger logger = LoggerFactory.getLogger(ClassNode.class);
    protected String parentClass;
    protected List<String> interfaceList;
    protected String qualifiedName;
    public File file;

    protected int line;
    @JsonIgnore
    protected List<InitNode> initNodes; //to save var & type when init

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public List<String> getInterfaceList() {
        return interfaceList;
    }

    public void setInterfaceList(List<String> interfaceList) {
        this.interfaceList = interfaceList;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public List<InitNode> getInitNodes() {
        return initNodes;
    }

    public void setInitNodes(List<InitNode> initNodes) {
        this.initNodes = initNodes;
    }

    public ClassNode() {
        super();
        interfaceList = new ArrayList<>();
        this.initNodes = new ArrayList<>();
    }


    @JsonIgnore
    public List<MethodNode> getMethodList() {
        List<MethodNode> result = new ArrayList<>();
        for (Node child : this.getChildren()) {
            if (child instanceof MethodNode)
                result.add((MethodNode) child);
        }
        return result;
    }

    @JsonIgnore
    public List<FieldNode> getFieldList() {
        List<FieldNode> result = new ArrayList<>();
        for (Node child : this.getChildren()) {
            if (child instanceof FieldNode)
                result.add((FieldNode) child);
        }
        return result;
    }

    @Override
    public String toString() {
        return "ClassNode {" +
                "id=" + this.id +
//                "visibility=" + this.getVisibility() +
//                ", type='" + type + '\'' +
                ", name=" + this.name +
//                ", isStatic=" + this.isStatic() +
//                ", isAbstract=" + this.isAbstract() +
//                ", isFinal=" + this.isFinal() +
//                ", isInterface=" + isInterface +
                ", parentClass='" + parentClass + '\'' +
                ", interfaceList=" + interfaceList +
                '}';
    }

    public void parseClasses(TypeDeclaration node, CompilationUnit cu) {
//        if (node.isInterface() == true) this.setInterface(true);
        Position position = ASTHelper.getPosition(node);
        this.setStartLine(ASTHelper.getLine(position.getStartPos(), cu));
        this.setEndLine(ASTHelper.getLine(position.getEndPos(), cu));

        //lay superClass
        Type superClassType = node.getSuperclassType();
        if (superClassType != null && superClassType instanceof SimpleType) {
            SimpleType superSimpleClassType = (SimpleType) superClassType;
            if (superSimpleClassType.getName() != null) {
                String fullname = ASTHelper.getFullyQualifiedTypeName(
                        null, superSimpleClassType.getName().getFullyQualifiedName(), cu);
                this.setParentClass(fullname);
            }
        }


        if (this.qualifiedName == null) {
            //lay ten
            if (node.getName() != null) {
                if (node.getName().getIdentifier() != null) {
                    String name = node.getName().getIdentifier();
                    this.setName(name);
                }
            }
            this.setQualifiedName(ASTHelper.getFullyQualifiedTypeName(null, this.getName(), cu));
        }

        //lay cac properties
        FieldDeclaration[] fieldList = node.getFields();
        List<Node> fieldNodes = Convert.parseFields(
                fieldList, this, cu, initNodes);

        this.addChildren(fieldNodes, cu);
        //lay cac methods
        MethodDeclaration[] methodList = node.getMethods();
        List<Node> methodNodes = Convert.parseMethods(this, methodList, cu);
        this.addChildren(methodNodes, cu);

        //TODO lay cac class con ben trong
        TypeDeclaration[] classList = node.getTypes();
        List<Node> innerClassNode = Convert.parseClasses(this.getName(), classList, cu);
        this.addChildren(innerClassNode, cu);

        List<Node> innerTmp = new ArrayList<>();
        innerTmp.add(this);
        innerTmp.addAll(innerClassNode);
        //parserStatementNodes
        parserStatements(methodNodes, innerTmp);

        //lay danh sach cac superInterface
        List superInterfaceList = node.superInterfaceTypes();
        if (superInterfaceList.size() > 0) {
            ArrayList<String> interfaceNameList = new ArrayList<String>();
            for (Object o : superInterfaceList) {
                if (o instanceof SimpleType) {
                    SimpleType intefaceType = (SimpleType) o;
                    String fullInterfaceName = ASTHelper.getFullyQualifiedName(this, intefaceType, cu);
                    interfaceNameList.add(fullInterfaceName);
                }
            }
            this.setInterfaceList(interfaceNameList);
        }
        // Lay cac enums
        List<Node> enumNodes = new ArrayList<>();
        for (Object obj : node.bodyDeclarations()) {
            if (obj instanceof EnumDeclaration) {
                EnumNode enumNode = Convert.convertToEnumNode(this.getName(), (EnumDeclaration) obj, cu);
                enumNodes.add(enumNode);
            }
        }
        this.addChildren(enumNodes, cu);
    }

    private void parserStatements(List<Node> methodNodes, List<Node> innerClasses) {
        for (Node node : methodNodes) {
            MethodNode methodNode = (MethodNode) node;
            methodNode.innerclasses = innerClasses;
            methodNode.parseStatements(1, methodNode.getStatements());
        }
    }

    public StatementNode findStatemmentByLine(List<Node> childs, int line) {
        for (Node node : childs) {
            if (node instanceof MethodNode) {
                if (node.getStartLine() <= line && node.getEndLine() >= line) {
                    return ((MethodNode) node).findStatementByLine(line);
                }
            } else if (node instanceof ClassNode) {
                StatementNode statementNode =  findStatemmentByLine(node.getChildren(), line);
                if (statementNode != null) {
                    return statementNode;
                }
            }
        }
        return null;
    }

        public int findIndexTypeVar (String varname){
            for (int i = 0; i < initNodes.size(); i++) {
                InitNode initNode = initNodes.get(i);

                if (initNode.getVarname().equals(varname)) {
                    return i;
                }

            }
            return -1;
        }

        public String findTypeVar (String varname, ClassNode parent){
            String result = null;
            for (int i = 0; i < initNodes.size(); i++) {
                InitNode initNode = initNodes.get(i);
                if (initNode.getVarname().equals(varname)) {
                    result = initNodes.get(i).getType();
                    return result;
                }
            }
            if (this instanceof EnumNode) {
                for (String enumConstant : ((EnumNode) this).getEnumConstants()) {
                    if (varname.equals(enumConstant)) {
                        return this.getQualifiedName();
                    }
                }
            }
            if (parent != null) {
                for (int i = 0; i < parent.initNodes.size(); i++) {
                    InitNode initNode = parent.initNodes.get(i);
                    if (initNode.getVarname().equals(varname)) {
                        result = parent.initNodes.get(i).getType();
                        return result;
                    }
                }
            }
            return result;
        }

//    public MethodNode findMethodNode(String methodName, List<String> params) {
//        List<MethodNode> methodNodes = new ArrayList<>();
//        for (MethodNode methodNode : getMethodList()) {
//            if (methodName.equals(methodNode.getName())) {
//                if (compareParams(methodNode.getParameters(), params))
//                    methodNodes.add(methodNode);
//            }
//        }
//        if (methodNodes.size() == 1) {
//            return methodNodes.get(0);
//        } else if (methodNodes.size() > 1) {
//            logger.error("THIS IS ERR");
//            return null;
//        }
//        return null;
//    }

        public MethodNode findMethodNode (String methodName, List < String > params){
            for (MethodNode methodNode : getMethodList()) {
                if (methodName.equals(methodNode.getName())) {
                    List<String> expected = new ArrayList<>();
                    for (ParameterNode p : methodNode.getParameters()) {
                        expected.add(p.getType());
                    }
                    if (ReflectionHelper.compareParams(expected, params)) {
                        return methodNode;
                    }
                }
            }
//        if (methodNodes.size() == 1) {
//            return methodNodes.get(0);
//        } else if (methodNodes.size() > 1) {
//            logger.error("THIS IS ERR");
//            return null;
//        }
            return null;
        }

        public MethodNode findMethodNodeByStmLine (String methodName,int line){
            for (MethodNode methodNode : getMethodList()) {
                if (methodName.equals(methodNode.getName())) {
                    if (line <= methodNode.getEndLine()
                            && line >= methodNode.getStartLine())
                        return methodNode;
                }
            }
            return null;
        }

    public MethodNode findMethodNodeInFile ( int line) {
        for (Node node : this.getChildren()) {
            if (node instanceof MethodNode) {
                if (line <= node.getEndLine()
                        && line >= node.getStartLine())
                    return (MethodNode) node;
            } else if (node instanceof ClassNode) {
                MethodNode methodNode = ((ClassNode) node).findMethodNodeByStmLine(line);
                if (methodNode != null) {
                    return methodNode;
                }
            }
        }
        return null;
    }

        public MethodNode findMethodNodeByStmLine ( int line){
            for (MethodNode methodNode : getMethodList()) {
                if (line <= methodNode.getEndLine()
                        && line >= methodNode.getStartLine())
                    return methodNode;
            }
            return null;
        }

        private boolean compareParams (List < ParameterNode > parameterNodes, List < String > params){
            boolean result = true;
            if (parameterNodes.size() == params.size()) {
                if (params.size() == 0) {
                    return true;
                }
                for (int i = 0; i < parameterNodes.size(); i++) {
                    if (params.get(i) != null) {
                        if (!params.get(i).equals(parameterNodes.get(i).getType())) {
                            result = false;
                            break;
                        }
                    }
                }
//            for (ParameterNode parameterNode : parameterNodes) {
//                for (String param : params) {
//                    if (param != null) {
//                        if (!param.equals(parameterNode.getType())) {
//                            result = false;
//                            break;
//                        }
//                    }
//                }
//            }
                return result;
            } else {
                return false;
            }
        }


    }
