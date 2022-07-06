package util;

import AST.node.*;
import AST.obj.Position;

import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ASTHelper {
    public static final Logger logger = LoggerFactory.getLogger(ASTHelper.class);

//    public static String findFieldType(FolderNode folderNode, ClassNode clazz, String fieldName) {
//        String type = null;
//        if (clazz != null) {
//            ClassNode parent = null;
//            if (clazz.getParentClass() != null) {
//                parent = folderNode.findClassByQualifiedName(clazz.getParentClass());
//            }
//            String initNode = clazz.findTypeVar(fieldName, parent);
//            if (initNode != null) {
//                type = initNode;
//            }
////            else {
////                ClassNode parent = folderNode.findClassByQualifiedName(clazz.getParentClass());
////                type = findFieldType(folderNode, parent, fieldName);
////            }
//        }
//        return type;
//    }

    public static int getLine(int postisition, CompilationUnit cu) {
        return cu.getLineNumber(postisition);
    }

//    public static String findMethodType(FolderNode folderNode, ClassNode classNode, MethodCalledNode node, List<String> params) {
//        String type = null;
//        boolean isEqual = true;
//        //find with BaseVar & methodInvo
//        if (classNode != null) {
//            List<MethodNode> methodNodes = classNode.findMethodNode(node.getMethodName(), params);
//            if (methodNodes.size() > 0) {
//                if (methodNodes.size() == 1) {
//                    for (int i = 0; i < node.getAgurements().size(); i++) {
//                        if (node.getAgurements().get(i).getType() == null) {
//                            (node.getAgurements().get(i))
//                                    .setType(methodNodes.get(0).getParameters().get(i).getType());
//
//                        }
//                    }
//                    return methodNodes.get(0).getReturnType();
//                } else {
//                    for (MethodNode methodNode : methodNodes) {
//                        if (type == null) {
//                            type = methodNode.getReturnType();
//                        } else {
//                            isEqual = type.equals(methodNode.getReturnType());
//                        }
//                    }
//                    if (isEqual) {
//                        return type;
//                    } else {
//                        logger.error("THIS IS ERROR TYPE");
//                        return null;
//                    }
//                }
//            } else {
//                ClassNode parent = folderNode.findClassByQualifiedName(classNode.getParentClass());
//                return findMethodType(folderNode, parent, node, params);
//            }
//        }
//        return null;
//    }

    public static String getFullyQualifiedName(ClassNode classNode, Type type, CompilationUnit cu) {
        if (type.isParameterizedType()) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return getFullyQualifiedTypeName(classNode, parameterizedType, cu);
        } else if (type.isArrayType()) {
            ArrayType arrayType = (ArrayType) type;
            return getFullyQualifiedTypeName(classNode, arrayType, cu);
        } else
            return getFullyQualifiedTypeName(classNode, type.toString(), cu);
    }

    public static Position getPosition(ASTNode astNode) {
        return new Position(astNode.getStartPosition(), astNode.getStartPosition() + astNode.getLength());
    }


    public static int getLine( ASTNode astNode, CompilationUnit cu) {
        return cu.getLineNumber(astNode.getStartPosition());
    }


    private static String getFullyQualifiedTypeName(ClassNode classNode, ParameterizedType parameterizedType, CompilationUnit cu) {
        String result = "";
        String type = parameterizedType.getType().toString();
        result += getFullyQualifiedTypeName(classNode, type, cu) + "<";

        List args = parameterizedType.typeArguments();
        if (args.size() == 1) {
            String argQualifiedName = getFullyQualifiedTypeName(classNode, args.get(0).toString(), cu);
            result += argQualifiedName;
        } else {
            for (Object arg : args) {
                if (arg instanceof ParameterizedType) {
                    String argype = getFullyQualifiedTypeName(classNode, (ParameterizedType) arg, cu);
                    result += argype + ",";
                } else {
                    String argQualifiedName = getFullyQualifiedTypeName(classNode, arg.toString(), cu);
                    result += argQualifiedName + ",";
                }
            }
            result = result.substring(0, result.length() - 1);
        }
        result += ">";
        return result;
    }

    private static String getFullyQualifiedTypeName(ClassNode classNode, ArrayType arrayType, CompilationUnit cu) {
        String result = "";
        result += getFullyQualifiedTypeName(classNode, arrayType.getElementType().toString(), cu);
        for (Object dimen : arrayType.dimensions()) {
            result += dimen.toString();
        }
        return result;
    }

    public static String getFullyQualifiedTypeName(ClassNode classNode,
                                                   String typeName, CompilationUnit cu) {

        //TODO: edit int -> Integer
        // is primitive type?
        if (primitiveTypes.contains(typeName)) {
            return typeName;
        }

        // find in import statements
        for (Object o : cu.imports()) {
            if (o instanceof ImportDeclaration) {
                ImportDeclaration id = (ImportDeclaration) o;
                String idStr = id.getName().getFullyQualifiedName();
                String[] path = typeName.split("\\.");
                String[] pathSub = idStr.split("\\.");
                if (idStr.endsWith("." + typeName)) {
                    //case Class.Type
                    if (path.length > 1) {
                        String field = ReflectionHelper.findFieldType(idStr.substring(0, idStr.lastIndexOf(".")),
                                path[path.length - 1], null);
                        if (field == null) {
                            if (Character.isUpperCase(path[path.length - 1].charAt(0)) &&
                                    Character.isUpperCase(path[path.length - 2].charAt(0))) {
                                String enumType = idStr.substring(0, idStr.lastIndexOf("."));
                                if (ReflectionHelper.isEnum(enumType)) {
                                    return enumType;
                                } else {
                                    return idStr.substring(0, idStr.lastIndexOf(".")) + "$" +
                                            idStr.substring(idStr.lastIndexOf(".") + 1);
                                }
                            }
                        } else {
                            return field;
                        }
                    } else {
                        //case Class
                        if (Character.isUpperCase(pathSub[pathSub.length - 1].charAt(0)) &&
                                Character.isUpperCase(pathSub[pathSub.length - 2].charAt(0))) {
                            String enumType = idStr.substring(0, idStr.lastIndexOf("."));
                            if (ReflectionHelper.isEnum(enumType)) {
                                return enumType;
                            } else {
                                return idStr.substring(0, idStr.lastIndexOf(".")) + "$" +
                                        idStr.substring(idStr.lastIndexOf(".") + 1);
                            }
                        }
                    }
                    return idStr;
                } else {
                    if (path.length > 1) {
                        if (idStr.endsWith("." + path[path.length - 2])) {
                            if (ReflectionHelper.isEnum(idStr)) {
                                return idStr;
                            } else {
                                return idStr + "$" + path[path.length - 1];
                            }
                        }
                    }
                }

            }
        }

        // In java.lang or java.util package
        if (javaLangTypes.contains(typeName)) {
            if (typeName.contains(".")) {
                typeName = typeName.replace(".", "$");
            }
            return "java.lang." + typeName;
        } else if (javaUtilTypes.contains(typeName)) {
            if (typeName.contains(".")) {
                typeName = typeName.replace(".", "$");
            }
            return "java.util." + typeName;
        } else if (javaJunitFrameworkTypes.contains(typeName)) {
            if (typeName.contains(".")) {
                typeName = typeName.replace(".", "$");
            }
            return "java.junit.framework." + typeName;
        }
        //find in Enum or Class are created in class
        for (Object obj : cu.types()) {
            if (obj instanceof TypeDeclaration) {
                String clsName = ((TypeDeclaration) obj).getName().getIdentifier();
                for (Object innerClass : ((TypeDeclaration) obj).bodyDeclarations()) {
                    if (innerClass instanceof TypeDeclaration) {
                        if (((TypeDeclaration) innerClass).getName().getIdentifier()
                                .equals(typeName)) {
                            return ASTHelper.getQualifiedNameInnerClass(clsName, ((TypeDeclaration) innerClass)
                                    .getName().getIdentifier(), cu);
                        }
                    } else if (innerClass instanceof EnumDeclaration) {
                        if (((EnumDeclaration) innerClass).getName().getIdentifier()
                                .equals(typeName)) {
                            return ASTHelper.getQualifiedNameInnerClass(clsName, ((EnumDeclaration) innerClass)
                                    .getName().getIdentifier(), cu);
                        }
                    }
                }
            } else if (obj instanceof EnumDeclaration) {
                if (((EnumDeclaration) obj).getName().getIdentifier()
                        .equals(typeName)) {
                    return ASTHelper.getQualifiedNameInnerClass(classNode.getName(), ((EnumDeclaration) obj)
                            .getName().getIdentifier(), cu);
                }
            }
        }

        if (typeName.contains(".")) {
            typeName = typeName.replace(".", "$");
        }
        PackageDeclaration packageDeclaration = cu.getPackage();
        if (packageDeclaration == null) {
            return typeName;
        } else {
            String type = packageDeclaration.getName() + "." + typeName;
            if (ReflectionHelper.isExist(type)) {
                return type;
            } else {
                //find in folder
                if (classNode != null) {
                    if (classNode.file != null) {
                        String newtype = getInFolder(
                                classNode.file, typeName, packageDeclaration.getName().getFullyQualifiedName());
                        if (newtype != null) {
                            return newtype;
                        }
                    }
                }
            }
            return type;
        }
    }

    private static String getInFolder(File file, String type, String packageName) {
        String newType = null;
        if (file.isFile()) {
            newType = getInFolder(file.getParentFile(), type, packageName);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    String tmp = packageName + "." + f.getName().replace(".java", "") + "$" + type;
                    if (ReflectionHelper.isExist(tmp)) {
                        return tmp;
                    }
                }
            }
        }
//        if (node instanceof FolderNode) {
//            List<Node> classNodes = node.getAllChildren();
//            for (Node n : classNodes) {
//                if (n instanceof ClassNode) {
//                    newType = ((ClassNode) n).getQualifiedName() + "$" + type;
//                    if (ReflectionHelper.isExist(newType)) {
//                        return newType;
//                    }
//                }
//            }
//        } else {
//            if (node != null) {
//                newType = getInFolder(node.getParent(), type);
//            }
//        }
        return newType;
    }

    public static String getFieldType(String classFullName, ClassNode className, String fieldName, Type typeNode, CompilationUnit cu) {
        String parseType;
        if (typeNode != null) {
            parseType = ASTHelper.getFullyQualifiedName(className, typeNode, cu);
        } else {
            parseType = ASTHelper.getFullyQualifiedTypeName(className, fieldName, cu);
        }
//        String findType = ReflectionHelper.findFieldType(classFullName, fieldName);
//        if (findType != null) {
//            if (parseType.contains(findType)) {
//                return parseType;
//            } else {
//                return findType; //
//            }
//        } else {
//            return parseType;
//        }
        return parseType;
    }

    protected static final String[] PRIMITIVE_TYPES = {
            "boolean", "short", "int", "long", "float", "double", "void", "char"
    };

    protected static final String[] JAVA_JUNIT_FRAMEWORK = {
            "TestCase"
    };

    protected static final String[] JAVA_LANG_TYPES = {
            "Boolean", "Byte", "Character", "Character.Subset", "Character.UnicodeBlock", "Class", "ClassLoader", "ClassValue", "Double",
            "Enum", "InheritableThreadLocal", "Float", "Integer", "Long", "Math", "Number", "Object", "Package", "Process", "Runtime",
            "Short", "String", "StringBuffer", "StringBuilder", "System", "Thread", "ThreadGroup",
            "Throwable", "Void", "ProcessBuilder", "ProcessBuilder.Redirect", "RuntimePermission", "SecurityManager", "StackTraceElement",
            "StrictMath", "ThreadLocal", "Character.UnicodeScript", "ProcessBuilder.Redirect.Type", "Thread.State", "Appendable", "AutoCloseable",
            "CharSequence", "Cloneable", "Comparable", "Iterable", "Readable", "Runnable", "Thread.UncaughtExceptionHandler"
    };

    protected static final String[] JAVA_UTIL_TYPES = {
            "Map.Entry", "Map", "Collection", "Comparator", "Deque", "Enumeration", "EventListener",
            "Formattable", "Iterator", "List", "ListIterator", "NavigableMap", "NavigableSet", "Observer",
            "PrimitiveIterator", "PrimitiveIterator.OfDouble", "PrimitiveIterator.OfInt", "PrimitiveIterator.OfLong",
            "Queue", "RandomAccess", "Set", "SortedMap", "SortedSet", "Spliterator", "Spliterator.OfDouble",
            "Spliterator.OfInt", "Spliterator.OfLong", "Spliterator.OfPrimitive", "AbstractCollection",
            "AbstractList", "AbstractMap", "AbstractMap.SimpleEntry", "AbstractMap.SimpleImmutableEntry",
            "AbstractQueue", "AbstractSequentialList", "AbstractSet", "ArrayDeque", "ArrayList", "Arrays",
            "Base64", "Base64.Decoder", "Base64.Encoder", "BitSet", "Calendar", "Calendar.Builder", "Collections",
            "Currency", "Date", "Dictionary", "DoubleSummaryStatistics", "EnumMap", "EnumSet", "EventListenerProxy",
            "EventObject", "FormattableFlags", "Formatter", "GregorianCalendar", "HashMap", "HashSet",
            "Hashtable", "IdentityHashMap", "IntSummaryStatistics", "LinkedHashMap", "LinkedHashSet", "LinkedList",
            "ListResourceBundle", "Locale", "Locale.Builder", "Locale.LanguageRange", "LongSummaryStatistics",
            "Objects", "Observable", "Optional", "OptionalDouble", "OptionalInt", "OptionalLong", "PriorityQueue",
            "Properties", "PropertyPermission", "PropertyResourceBundle", "Random", "ResourceBundle",
            "ResourceBundle.Control", "Scanner", "ServiceLoader", "SimpleTimeZone", "Spliterators", "Spliterators.AbstractDoubleSpliterator",
            "Spliterators.AbstractIntSpliterator", "Spliterators.AbstractLongSpliterator", "Spliterators.AbstractSpliterator",
            "SplittableRandom", "Stack", "StringJoiner", "StringTokenizer", "Timer", "TimerTask", "TimeZone",
            "TreeMap", "TreeSet", "UUID", "Vector", "WeakHashMap"
    };

    public static String getType(String type) {

        if (type.equals("boolean")) {
            return "Boolean";
        } else if (type.equals("short")) {
            return "Short";
        } else if (type.equals("int")) {
            return "Integer";
        } else if (type.equals("long")) {
            return "Long";
        } else if (type.equals("float")) {
            return "Float";
        } else if (type.equals("double")) {
            return "Double";
        } else {
            return type;
        }
    }

    protected static List<String> primitiveTypes = Arrays.asList(PRIMITIVE_TYPES);
    protected static List<String> javaLangTypes = Arrays.asList(JAVA_LANG_TYPES);
    protected static List<String> javaUtilTypes = Arrays.asList(JAVA_UTIL_TYPES);
    protected static List<String> javaJunitFrameworkTypes = Arrays.asList(JAVA_JUNIT_FRAMEWORK);


    public static String getQualifiedNameInnerClass(String className,
                                                    String objectInnerClass,
                                                    CompilationUnit cu) {
        PackageDeclaration packageDeclaration = cu.getPackage();
        if (packageDeclaration != null) {
            if (packageDeclaration.getName() != null) {
                if (packageDeclaration.getName().getFullyQualifiedName() != null) {
                    if (objectInnerClass.contains(".")) {
                        objectInnerClass = objectInnerClass.replace(".", "$");
                    }
                    if (className == null) {
                        return packageDeclaration.getName().getFullyQualifiedName()
                                + "." + objectInnerClass;
                    } else {
                        return packageDeclaration.getName().getFullyQualifiedName()
                                + "." + className + "$" + objectInnerClass;
                    }
                }
            }
        }
        if (className != null) {
            objectInnerClass = className + "." + objectInnerClass;
        }
        return objectInnerClass;
    }

}
