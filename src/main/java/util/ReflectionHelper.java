package util;

import AST.node.ClassNode;
import AST.node.MethodNode;
import AST.node.Node;
import AST.stm.abst.StatementNode;
import AST.stm.token.ClassInstanceCreationNode;
import AST.stm.node.TypeNode;
import AST.stm.nodetype.InitNode;
import AST.stm.token.BaseVariableNode;
import AST.stm.token.MethodCalledNode;
import AST.stm.token.QualifiedNameNode;
import common.DefineType;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ReflectionHelper {
    private static URLClassLoader classLoader = null;

    /**
     * ================================================
     * Find type of AST Treee
     * =================================================
     */
    public static void initClassLoader(String srcPath) {
        File plugins[] = new File(srcPath + "/build/lib").listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".jar");
            }
        });
        File plugins2[] = new File(srcPath + "/lib").listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".jar");
            }
        });

        try {
            //jar files
            List<URL> plugInURLs = new ArrayList<>();
            if (plugins != null) {
                for (File plugin : plugins) {
                    plugInURLs.add(plugin.toURI().toURL());
                }
            }
            if (plugins2 != null) {
                for (File plugin : plugins2) {
                    plugInURLs.add(plugin.toURI().toURL());
                }
            }

            //build folder
            File customElementsDir = new File(srcPath + "/build/classes");
            if (customElementsDir.exists()) {
                URL url = customElementsDir.toURI().toURL();
                plugInURLs.add(url);
            } else {
                File buildFile = new File(srcPath + "/build/");
                if (buildFile.exists()) {
                    URL url = buildFile.toURI().toURL();
                    plugInURLs.add(url);
                }
            }

            File buildFileIntel = new File(srcPath + "/target/classes");
            if (buildFileIntel.exists()) {
                URL url = buildFileIntel.toURI().toURL();
                plugInURLs.add(url);
            }

            File test = new File(srcPath + "/build/test");
            if (test.exists()) {
                URL urlTest = test.toURI().toURL();
                plugInURLs.add(urlTest);
            } else {
                File testFolder = new File(srcPath + "/build-tests");
                if (testFolder.exists()) {
                    URL urlTest = testFolder.toURI().toURL();
                    plugInURLs.add(urlTest);
                }
            }

            File testFolder = new File(srcPath + "/target/tests");
            if (testFolder.exists()) {
                URL urlTest = testFolder.toURI().toURL();
                plugInURLs.add(urlTest);
            }

//            Collections.reverse(plugInURLs);
            //add all to List
            URL[] urls = plugInURLs.toArray(new URL[0]);
            classLoader = new URLClassLoader(urls);
//            Class a  = classLoader.loadClass("com.google.javascript.rhino.testing.TestErrorReporter");
//            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String findFieldType(String classQualifiedName,
                                       String fieldName, MethodNode methodNode) {
        if (getClass(classQualifiedName) != null) {
            return findFieldType(classQualifiedName, fieldName);
        } else {
            // private static class
            if (methodNode != null) {
                for (Node inner : methodNode.innerclasses) {
                    if (inner instanceof ClassNode) {
                        if (((ClassNode) inner).getQualifiedName()
                                .equals(classQualifiedName)) {
                            // find fields
                            return findFieldInInit(methodNode, (ClassNode) inner, fieldName);
                        }
                    }
                }
            }
        }
        //find in parent
        //TODO: error in Lang 6
        if (classQualifiedName.contains("$")) {
            String newClassQualifiedName = classQualifiedName
                    .substring(0, classQualifiedName.indexOf("$"));
            return findFieldType(newClassQualifiedName, fieldName, methodNode);
        }
        return null;
    }

    public static String findMethodType(String classQualifiedName, MethodCalledNode node,
                                        List<String> params, MethodNode methodNode, CompilationUnit cu) {
        if (ReflectionHelper.getClass(classQualifiedName) != null) {
            Method method = ReflectionHelper.findMethodType(classQualifiedName, node.getMethodName(),
                    params);
            if (method != null) {
                if (method.getParameterTypes().length == node.getArguments().size()) {
                    for (int i = 0; i < node.getArguments().size(); i++) {
                        if (node.getArguments().get(i).getType() == null) {
                            node.getArguments().get(i).setType(
                                    method.getGenericParameterTypes()[i].getTypeName());
                        }
                    }
                }
                return method.getGenericReturnType().getTypeName().replace(" ", "");
            } else {
                return ASTHelper.getFullyQualifiedTypeName(null, node.getMethodName(), cu);
            }
        } else {
            //private static class
            for (Node innerClass : methodNode.innerclasses) {
                if (innerClass instanceof ClassNode) {
                    if (((ClassNode) innerClass).getQualifiedName()
                            .equals(classQualifiedName)) {
                        MethodNode method = ((ClassNode) innerClass)
                                .findMethodNode(node.getMethodName(),
                                        params);
                        if (method != null) {
                            if (method.getParameters().size() == node.getArguments().size()) {
                                for (int i = 0; i < node.getArguments().size(); i++) {
                                    if (node.getArguments().get(i).getType() == null) {
                                        node.getArguments().get(i).setType(
                                                method.getParameters().get(i).getType());
                                    }
                                }
                            }
                            return method.getReturnType();
                        }
                    }
                }
            }
        }
        //find in parent
        //TODO: error in Lang 6
        if (classQualifiedName.contains("$")) {
            String newClassQualifiedName = classQualifiedName
                    .substring(0, classQualifiedName.indexOf("$"));
            return findMethodType(newClassQualifiedName, node, params, methodNode, cu);
        }
        return null;
    }

    public static String findFieldType(String classQualifiedName, String fieldName) {
        if (classQualifiedName.contains("<") & classQualifiedName.contains(">")) {
            classQualifiedName = classQualifiedName.substring(0, classQualifiedName.indexOf("<"));
        } else if (classQualifiedName.contains("[") & classQualifiedName.contains("]")) {
            if (DefineType.types.containsKey(fieldName)) {
                return DefineType.types.get(fieldName);
            }
        }
        try {
            Class<?> clazz = getClass(classQualifiedName);
            if (clazz.isEnum()) {
                for (Object obj : clazz.getEnumConstants()) {
                    if (obj.toString().equals(fieldName)) {
                        return clazz.getTypeName();
                    }
                }
            } else {
                Field[] fields = getAllFieldsInHierarchy(clazz);
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        return field.getGenericType().getTypeName().replace(" ", "");
                    }
                }
                Class[] classes = clazz.getDeclaredClasses();
                for (Class clas : classes) {
                    Field[] fields1 = clas.getFields();
                    for (Field field : fields1) {
                        if (field.getName().equals(fieldName)) {
                            return field.getGenericType().getTypeName().replace(" ", "");
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                if (classQualifiedName.contains(".")) {
                    classQualifiedName = classQualifiedName
                            .substring(0, classQualifiedName.lastIndexOf(".")) + "$" +
                            classQualifiedName.substring(classQualifiedName.lastIndexOf(".") + 1);
                    String type = ReflectionHelper.findFieldType(classQualifiedName,
                            fieldName);
                    if (type != null) {
                        return type;
                    }
                }
            }
        } catch (IllegalAccessError error) {
            return null;
        }
        if (classQualifiedName.contains("$")) {
            return findFieldType(classQualifiedName.substring(0,
                    classQualifiedName.lastIndexOf("$")), fieldName);
        }
        return null;
    }

    private static Method findMethodType(String classQualifiedName, String methodName, List<String> parameterTypes) {
        List<Method> resultMethods = new ArrayList<>();
        if (classQualifiedName.contains("<") & classQualifiedName.contains(">")) {
            classQualifiedName = classQualifiedName.substring(0,
                    classQualifiedName.indexOf("<"));
        } else if (classQualifiedName.contains("[") & classQualifiedName.contains("]")) {
            classQualifiedName = classQualifiedName.substring(0,
                    classQualifiedName.indexOf("["));
        }
        try {
            Class<?> clazz = getClass(classQualifiedName);
            Method[] methods = getAllMethodsInHierarchy(clazz);
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    List<String> paramsExpected = new ArrayList<>();
                    for (Type param : method.getGenericParameterTypes()) {
                        paramsExpected.add(param.getTypeName().replace(" ", ""));
                    }
                    if (compareParams(paramsExpected, parameterTypes)) {
                        return method;
                    }
                    resultMethods.add(method);
                }
            }
            if (classQualifiedName.contains("$")) {
                String newClassQualifiedName = classQualifiedName.substring(0,
                        classQualifiedName.indexOf("$"));
                return findMethodType(newClassQualifiedName, methodName, parameterTypes);
            }
//            Class[] classes = new Class[parameterTypes.size()];
//            for (String param : parameterTypes) {
//                classes[parameterTypes.indexOf(param)] = Class.forName(param);
//            }
//            Method method = clazz.getDeclaredMethod(methodName, classes);
//            method.setAccessible(true);
//            if (method != null) {
//                return method;
//            }
            Class<?> objectCLass = getClass("java.lang.Object");
            if (objectCLass != null) {
                //TODO: just no params toString(), hash(),
                // need to multi param eg: hash(a, b)
                return objectCLass.getDeclaredMethod(methodName);
//                if (parameterTypes.size() == 0) {
//                    return objectCLass.getDeclaredMethod(methodName);
//                } else {
//                    return objectCLass.getDeclaredMethod(methodName, parameterTypes.getClass());
//                }
            }
        } catch (Exception e) {
            if (resultMethods.size() == 1) {
                return resultMethods.get(0);
            } else {
                if (resultMethods.size() > 1) {
                    for (Method method : resultMethods) {
                        for (Type param : method.getGenericParameterTypes()) {
                            if (param.getTypeName().replace(" ", "").contains("[]")) {
                                return method;
                            }
                        }
                    }
                    int max = -1;
                    int indexMax = -1;
                    for (Method method : resultMethods) {
                        int index = resultMethods.indexOf(method);
                        List<String> paramsExpected = new ArrayList<>();
                        for (Type param : method.getGenericParameterTypes()) {
                            paramsExpected.add(param.getTypeName().replace(" ", ""));
                        }
                        int scoreMethod = countParams(paramsExpected, parameterTypes);
                        if (scoreMethod > max) {
                            max = scoreMethod;
                            indexMax = index;
                        } else if (scoreMethod == max) {
                            //case: abtract and class implements => get same classQualifiedName
                            if (method.getDeclaringClass().getName()
                                    .equals(classQualifiedName)) {
                                indexMax = index;
                            }
                        }
                    }
                    if (max != 0) {
                        return resultMethods.get(indexMax);
                    }
                }
                return null;
            }
        }
        return null;
    }


    public static String findMethodCalledNodeType(MethodCalledNode node,
                                                  StatementNode parentNode) {
        List<String> typeParams = new ArrayList<>();
        List<StatementNode> params = node.getArguments();
        if (params != null) {
            for (StatementNode param : params) {
                typeParams.add(param.getType());
            }
        }
        String type = null;
        if (parentNode.getType() != null) {
            Method method = ReflectionHelper.findMethodType(
                    parentNode.getType(), node.getMethodName(),
                    typeParams);
            if (method != null) {
                //set params for null method
                if (params.size() == method.getParameterTypes().length) {
                    for (int i = 0; i < params.size(); i++) {
                        if (params.get(i).getType() == null
                                || params.get(i).getType().equals("java.lang.Object")) {
                            String newType = method.getParameterTypes()[i].getName();
                            node.getArguments().get(i).setType(newType);
                        }
                    }
                }
                type = method.getGenericReturnType().getTypeName().replace(" ", "");
                String parentType = parentNode.getType();
                if (type.equals("java.lang.Object") || (type.length() == 1 && Character.isUpperCase(type.charAt(0)))) {
                    if (method.getName().equals("put")) {
                        if (parentType.contains("<") && !parentType.contains(",")) {
                            type = parentType.substring(parentType.indexOf("<") + 1);
                            type = type.substring(0, type.length() - 1);
                        } else if (parentType.contains("<") && parentType.contains(",")) {
                            type = parentType;
                        }
                    } else {
                        if (parentType.contains("java.util.List<")
                                || parentType.contains("java.util.ArrayList<")
                                || parentType.contains("java.util.LinkedList<")) {
                            //java.util.List<String>
                            if (node.getMethodName().equals("get")) {
                                type = parentType.substring(parentType.indexOf("<") + 1);
                                type = type.substring(0, type.length() - 1);
                            } else if (node.getMethodName().equals("set")
                                    || node.getMethodName().equals("pop")) {
                                type = "void";
                            } else if (node.getMethodName().equals("toArray")) {
                                type = parentType.substring(parentType.indexOf("<") + 1);
                                type = type.substring(0, type.length() - 1) + "[]";
                            }
                        } else if (parentType.contains("java.util.Map<")
                                || parentType.contains("java.util.SortedMap<")
                                || parentType.contains("java.util.HashMap<")
                                || parentType.contains("java.lang.ThreadLocal<")
                                || parentType.contains("java.util.concurrent.ConcurrentMap<")) {
                            //java.util.Map<String,String>
                            if (node.getMethodName().equals("get") || node.getMethodName().equals("put")) {
                                type = parentType.substring(parentType.indexOf(",") + 1);
                                type = type.substring(0, type.length() - 1);
                            }
                        } else if (parentType.contains("java.util.Map$Entry<")) {
                            //java.util.Map$Entry<String,String>.
                            if (node.getMethodName().equals("getKey")) {
                                type = parentType.substring(parentType.indexOf("<") + 1,
                                        parentType.indexOf(","));
                            }
                        } else if (parentType.contains("java.util.Iterator<")) {
                            //java.util.Iterator<com.google.javascript.rhino.jstype.JSType>
                            if (node.getMethodName().equals("next")) {
                                type = parentType.substring(parentType.indexOf("<") + 1);
                                type = type.substring(0, type.length() - 1);
                            }
                        } else {
                            if (parentType.contains("<") && !parentType.contains(",")) {
                                type = parentType.substring(parentType.indexOf("<") + 1);
                                type = type.substring(0, type.length() - 1);
                            } else if (parentType.contains("<") && parentType.contains(",")) {
                                type = parentType.substring(parentType.indexOf(",") + 1);
                                type = type.substring(0, type.length() - 1);
                            }
//                    }
                        }
                    }
                }
                node.setType(type);
            } else {
//                System.out.println();
            }
        }
        return type;
    }


    public static boolean isExist(String classFullName) {
        Class clzz = getClass(classFullName);
        if (clzz != null) {
            return true;
        } else {
            return false;
        }
    }


    private static int countParams(List<String> paramExpected, List<String> paramIn) {
        int score = 0;
        //format
//        for (int i = 0; i < paramIn.size(); i++) {
//            if (paramIn.get(i) != null) {
//                String actualParam = formatActualType(paramIn.get(i));
//                paramIn.set(i, actualParam);
//            }
//        }
        if (paramExpected.size() == paramIn.size()) {
            score++;
            for (int i = 0; i < paramExpected.size(); i++) {
                if (paramIn.get(i) != null) {
                    Class real = null;
                    Class expected = null;
                    String expectedParam = paramExpected.get(i);

                    String actualParam = paramIn.get(i);

                    try {
                        real = classLoader.loadClass(actualParam);
                        expected = classLoader.loadClass(expectedParam);
                    } catch (Exception e) {
                    }
                    if (real != null && expected != null) {
                        //can assign expected from real (using for check)
                        if (expected.isAssignableFrom(real)) {
                            score++;
                        }
                    } else if (expectedParam.equals("java.lang.Object")) {
                        continue;
                    } else if (paramIn.get(i) != null) {
                        if (expectedParam.equals(actualParam)) {
                            score++;
                        }
                    }
                }
            }
        } else {
            for (String pamam : paramIn) {
                if (paramExpected.contains(pamam)) {
                    score++;
                }
            }
        }
        return score;
    }


    /**
     * Gets an array of all methods in a class hierarchy walking up to parent classes
     *
     * @param objectClass the class
     * @return the methods array
     */
    public static Method[] getAllMethodsInHierarchy(Class<?> objectClass) {
        Set<Method> allMethods = new HashSet<Method>();
        if (objectClass != null) {
            Method[] declaredMethods = objectClass.getDeclaredMethods();
            Method[] methods = objectClass.getMethods();
            allMethods.addAll(Arrays.asList(methods));
            allMethods.addAll(Arrays.asList(declaredMethods));
            if (objectClass.getSuperclass() != null) {
                Class<?> superClass = objectClass.getSuperclass();
                Method[] superClassMethods = getAllMethodsInHierarchy(superClass);
                allMethods.addAll(Arrays.asList(superClassMethods));
            }
        }
        return allMethods.toArray(new Method[allMethods.size()]);
    }

    public static Field[] getAllFieldsInHierarchy(Class<?> type) {
        Set<Field> allFields = new HashSet<Field>();
        allFields.addAll(Arrays.asList(type.getDeclaredFields()));

        allFields.addAll(Arrays.asList(type.getFields()));

        if (type.getSuperclass() != null) {
            Field[] parentFields = getAllFieldsInHierarchy(type.getSuperclass());
            allFields.addAll(Arrays.asList(parentFields));
        }

        return allFields.toArray(new Field[allFields.size()]);
    }

    public static boolean compareParams(List<String> paramExpected, List<String> paramIn) {
        boolean result = true;
        //format
//        for (int i = 0; i < paramIn.size(); i++) {
//            if (paramIn.get(i) != null) {
//                String actualParam = formatActualType(paramIn.get(i));
//                paramIn.set(i, actualParam);
//            }
//        }
        if (paramExpected.size() == paramIn.size()) {
            if (paramIn.size() == 0) {
                return true;
            }
            for (int i = 0; i < paramExpected.size(); i++) {
                if (paramIn.get(i) != null) {
                    Class real = null;
                    Class expected = null;
                    String expectedParam = paramExpected.get(i);
//                    if (expectedParam.contains("[")) {
//                        expectedParam = expectedParam.substring(0, expectedParam.indexOf("["));
//                    }
                    String actualParam = paramIn.get(i);
//                    if (actualParam.contains("[")) {
//                        actualParam = actualParam.substring(0, actualParam.indexOf("["));
//                    }
                    try {
                        real = Character.isUpperCase(actualParam.charAt(0)) ? getClass(actualParam) : null;
                        expected = Character.isUpperCase(expectedParam.charAt(0)) ? getClass(expectedParam) : null;
                    } catch (Exception e) {
                    }
                    if (real != null && expected != null) {
                        //can assign expected from real (using for check)
                        if (!expected.isAssignableFrom(real)) {
                            result = false;
                            break;
                        }
                    } else if (paramIn.get(i) != null) {
                        if (expectedParam != null) {
                            if (!expectedParam.equals(actualParam)) {
                                result = false;
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else if (expected.equals("java.lang.Object")) {
                        continue;
                    }
                }
            }
            return result;
        } else {
            List<String> tmp = new ArrayList<>(paramExpected);
            for (String param : paramExpected) {
                if (param != null) {
                    if (param.contains("[]")) {
                        tmp.remove(param);
                    }
                }
            }
            if (paramIn.size() == tmp.size()) {
                return compareParams(tmp, paramIn);
            } else {
                return false;
            }
        }
    }

//    private static boolean compareParam(String paramExpected, String paramActual) {
//        if (paramExpected.contains("[")) {
//            String arrayChar = "";
//            String nameType = "";
//            //format param expected
//            for (char c : paramExpected.toCharArray()) {
//                if (c == '[') {
//                    arrayChar += "[]";
//                } else if (c == 'Z') {
//                    nameType = "boolean";
//                    break;
//                } else if (c == 'B') {
//                    nameType = "byte";
//                    break;
//                } else if (c == 'C') {
//                    nameType = "char";
//                    break;
//                } else if (c == 'D') {
//                    nameType = "double";
//                    break;
//                } else if (c == 'F') {
//                    nameType = "float";
//                    break;
//                } else if (c == 'I') {
//                    nameType = "int";
//                    break;
//                } else if (c == 'J') {
//                    nameType = "long";
//                    break;
//                } else if (c == 'S') {
//                    nameType = "short";
//                    break;
//                } else if (c == 'L') {
//                    nameType = paramExpected.substring(paramExpected.indexOf(c) + 1).replace(";", "");
//                    break;
//                }
//            }
//            String type = nameType + arrayChar;
//            return type.equals(paramActual);
//        } else {
//            boolean isEqual = paramActual.equals(paramExpected);
//            if (!isEqual) {
//                if (paramActual.contains("<")) {
//                    String newParam = paramActual.substring(0, paramActual.indexOf("<"));
//                    isEqual = newParam.equals(paramExpected);
//                }
//            }
//            return isEqual;
//        }
//    }

    public static String formatExpectedType(String type) {
        if (type.contains("[")) {
            String arrayChar = "";
            String nameType = "";
            //format param expected
            for (char c : type.toCharArray()) {
                if (c == '[') {
                    arrayChar += "[]";
                } else if (c == 'Z') {
                    nameType = "boolean";
                    break;
                } else if (c == 'B') {
                    nameType = "byte";
                    break;
                } else if (c == 'C') {
                    nameType = "char";
                    break;
                } else if (c == 'D') {
                    nameType = "double";
                    break;
                } else if (c == 'F') {
                    nameType = "float";
                    break;
                } else if (c == 'I') {
                    nameType = "int";
                    break;
                } else if (c == 'J') {
                    nameType = "long";
                    break;
                } else if (c == 'S') {
                    nameType = "short";
                    break;
                } else if (c == 'L') {
                    nameType = type.substring(type.indexOf(c) + 1).replace(";", "");
                    break;
                }
            }
            type = nameType + arrayChar;
        }
        return type;
    }

//    private static String formatActualType(String type) {
//        if (type.contains("<")) {
//            type = type.substring(0, type.indexOf("<"));
//        }
//        return type;
//    }

    public static boolean isEnum(String enumType) {
        Class clazz = getClass(enumType);
        if (clazz != null) {
            return clazz.isEnum();
        }
        return false;
    }

    /**
     * ===========================================================
     *          END Find type of AST tree
     * ===========================================================
     */

    /**
     * ===========================================================
     *          Start Find Candidate same type
     * ===========================================================
     */

    /**
     * Finding candidate for enums
     *
     * @param enumClass
     * @return
     */
    public static List<StatementNode> findEnum(String enumClass) {
        List<StatementNode> enums = new ArrayList<>();

        Class clazz = getClass(enumClass);
        if (clazz.isEnum()) {
            Object[] objects = clazz.getEnumConstants();
            for (Object obj : objects) {
                String keyVar = enumClass.replace("$", ".");
                BaseVariableNode qualifier = new BaseVariableNode(keyVar);
                BaseVariableNode name = new BaseVariableNode(obj.toString());
                QualifiedNameNode qualifiedNameNode = new QualifiedNameNode(qualifier, name);
                qualifiedNameNode.setType(enumClass);
                qualifiedNameNode.setStatementString(keyVar + "." + obj.toString());
                enums.add(qualifiedNameNode);
//                enums.add(parentString + obj.toString());
            }
        }

        return enums;
    }


    private static String findFieldInInit(MethodNode methodNode, ClassNode classNode, String identifier) {
        for (InitNode initNode : methodNode.getInitNodes()) {
            if (initNode.getVarname().equals(identifier)) {
                return initNode.getType();
            }
        }
        //in class
        for (InitNode initNode : classNode.getInitNodes()) {
            if (initNode.getVarname().equals(identifier)) {
                return initNode.getType();
            }
        }
        return null;
    }

    // fields != QualifiedName
    private static List<BaseVariableNode> findBaseVarInHierarchySameType(String fullClassQualifiedName, String type) {
        HashMap<String, BaseVariableNode> baseVars = new HashMap<>();
        Class clazz = getClass(fullClassQualifiedName);
        if (clazz != null) {
            if (clazz.getSuperclass() != null) {
                Field[] fields = getAllFieldsInHierarchy(clazz.getSuperclass());
                for (Field field : fields) {
                    if (field.getType().getName().equals(type)) {
                        //field parent != private
                        if (!Modifier.isPrivate(field.getModifiers())) {
                            if (!baseVars.containsKey(field.getName())) {
                                BaseVariableNode baseVariableNode = new BaseVariableNode(field.getName());
                                baseVariableNode.setType(field.getGenericType().getTypeName().replace(" ", ""));
                                baseVars.put(field.getName(), baseVariableNode);
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(baseVars.values());
    }

    //    private static void getParentHierarchy(List<String> fields, Class parentType, String type) {
//        //public
//        for (Field field : parentType.getFields()) {
//            if (field.getType().getName().equals(type)) { //equals type
//                if (!fields.contains(field.getName())) { // duplicate
//                    fields.add(field.getName());
//                }
//            }
//        }
//        // != private
//        for (Field field : parentType.getDeclaredFields()) {
//            if (field.getType().getName().equals(type)) { //equal type
//                if (!Modifier.isPrivate(field.getModifiers())) { //not private
//                    if (!fields.contains(field)) { //duplicate
//                        fields.add(field.getName());
//                    }
//                }
//            }
//        }
//        if (parentType.getSuperclass() != null) {
//            getParentHierarchy(fields, parentType.getSuperclass(), type);
//        }
//    }

    public static List<MethodCalledNode> findMethodCalledSameType(String parentClassfullName, String type, ClassNode classNode) {
        List<MethodCalledNode> statementNodes = new ArrayList<>();
        if (parentClassfullName == null) {
            // same class
            statementNodes = findMethodSameTypeInSameClass(classNode, type);
        } else if (parentClassfullName.equals(classNode.getQualifiedName())) {
            // same class
            statementNodes = findMethodSameTypeInSameClass(classNode, type);
        } else {
            //diff class
            statementNodes = findMethodSameTypeInOtherClass(parentClassfullName, type);
        }
        if (parentClassfullName != null) {
            //find method in parent class
            if (parentClassfullName.contains("$") && statementNodes.size() == 0) {
                String newParentClass = parentClassfullName.substring(0, parentClassfullName.indexOf('$'));
                statementNodes = findMethodCalledSameType(newParentClass, type, classNode);
            }
        }
        return statementNodes;
    }

    private static List<MethodCalledNode> findMethodSameTypeInSameClass(ClassNode classNode, String type) {
        List<MethodCalledNode> statementNodes = new ArrayList<>();
        for (MethodNode methodNode : classNode.getMethodList()) {
            if (methodNode.getReturnType().equals(type)) {
                //add token method
                MethodCalledNode methodCalledNode = new MethodCalledNode(methodNode.getName());
                if (methodNode.getParameters().size() == 0) {
                    methodCalledNode.setStatementString(methodNode.getName() + "()");
                } else {
                    methodCalledNode.addParameterParams(methodNode.getParameters());
                }
                methodCalledNode.findInClass = false;
                methodCalledNode.setType(methodNode.getReturnType());
                statementNodes.add(methodCalledNode);
            }
        }
        return statementNodes;
    }

    private static List<MethodCalledNode> findMethodSameTypeInOtherClass(String classfullName, String type) {
        List<MethodCalledNode> statementNodes = new ArrayList<>();
        Class clazz = getClass(classfullName);
        Method[] methods = getAllMethodsInHierarchy(clazz);
        for (Method method : methods) {
            //return token of a method
            if (!Modifier.isPrivate(method.getModifiers())) {
                if (method.getGenericReturnType().getTypeName()
                        .replace(" ", "")
                        .equals(type)) {
                    Type[] genericParameterTypes = method.getGenericParameterTypes();
                    MethodCalledNode methodCalledNode = new MethodCalledNode(method.getName());
                    if (genericParameterTypes.length == 0) {
                        methodCalledNode.setStatementString(methodCalledNode.getMethodName() + "()");
                    }
                    methodCalledNode.setType(method.getGenericReturnType().getTypeName().replace(" ", ""));
                    //add token params
                    for (Type genericParameterType : genericParameterTypes) {
                        TypeNode typeNode = new TypeNode(genericParameterType.getTypeName()
                                .replace(" ", ""));
                        methodCalledNode.getArguments().add(typeNode);
                    }
                    methodCalledNode.findInClass = false;
                    // add token method
                    statementNodes.add(methodCalledNode);
                }
            }
        }

        return statementNodes;
    }

    public static List<ClassInstanceCreationNode> findConstructorSameType(String classNode, int paramSize) {
        List<ClassInstanceCreationNode> classInstanceCreationNodes = new ArrayList<>();
        Class clazz = getClass(classNode);
        if (clazz != null) {
            Constructor[] constructors = getConstructorInHierarchy(clazz);
            if (constructors != null) {
                for (Constructor constructor : constructors) {
                    ClassInstanceCreationNode classInstanceCreationNode = new ClassInstanceCreationNode(classNode,
                            constructor.toGenericString());
                    if (constructor.getGenericParameterTypes().length <= paramSize + 1) {
                        for (Type type : constructor.getGenericParameterTypes()) {
                            TypeNode typeNode = new TypeNode(type.getTypeName().replace(" ", ""));
                            classInstanceCreationNode.addParam(typeNode);
                        }
                    }
                    classInstanceCreationNode.findInClass = false;
                    classInstanceCreationNodes.add(classInstanceCreationNode);
                }
            }
        }
        return classInstanceCreationNodes;
    }

    public static Constructor[] getConstructorInHierarchy(Class clazz) {
        Set<Constructor> allConstructors = new HashSet<Constructor>();
        allConstructors.addAll(Arrays.asList(clazz.getConstructors()));

        if (clazz.getSuperclass() != null) {
            Constructor[] parentConstructs = getConstructorInHierarchy(clazz.getSuperclass());
            allConstructors.addAll(Arrays.asList(parentConstructs));
        }


        return allConstructors.toArray(new Constructor[allConstructors.size()]);
    }

    public static boolean compareType(String expectedType, String actualType) {
        Class real = null;
        Class expected = null;
        String formatEx = expectedType;
        String formatActual = actualType;
        boolean isCheckCLass = true;
        if (expectedType.contains("<") && actualType.contains("<")) {
            isCheckCLass = false;
        }
        if (isCheckCLass) {
            if (expectedType.contains("<")) {
                formatEx = expectedType.substring(0, expectedType.indexOf('<'));
            }
            if (actualType.contains("<")) {
                formatActual = actualType.substring(0, actualType.indexOf('<'));
            }
            try {
                real = getClass(formatEx);
                expected = getClass(formatActual);
            } catch (Exception e) {
            }
        }
        if (real != null && expected != null) {
            //can assign expected from real (using for check)
            if (real.isAssignableFrom(expected) || expected.isAssignableFrom(real)) {
                return true;
            }
        } else if (actualType != null) {
            if (expectedType.equals(actualType)) {
                return true;
            }
        }
        return false;
    }

    public static List<BaseVariableNode> findBaseVarSameType(String parentClassfullName, String type, int line, MethodNode methodNode) {
        HashMap<String, BaseVariableNode> map = new HashMap<>();
        boolean isInClass = false;
        boolean isInMethod = false;

        if (parentClassfullName == null) {
            //in class
            isInClass = true;
            isInMethod = true; //in method
        } else {
            if (parentClassfullName.equals(((ClassNode) methodNode.getParent())
                    .getQualifiedName())) {
                isInClass = true;
            } else {
                //with other class
                List<BaseVariableNode> baseVarsInParent = findBaseVarInHierarchySameType(parentClassfullName, type);
                for (BaseVariableNode baseVar : baseVarsInParent) {
                    if (!map.containsKey(baseVar.getKeyVar())) {
                        map.put(baseVar.getKeyVar(), baseVar);
                    }
                }
            }
        }
        if (isInClass) {
            //in method
            if (isInMethod) {
                for (InitNode initNode : methodNode.getInitNodes()) {
                    if (initNode.getLine() < line) {
                        if (compareType(type, initNode.getType())) {
                            if (!map.containsKey(initNode.getVarname())) {
                                BaseVariableNode baseVariableCandidate = new BaseVariableNode(initNode.getVarname());
                                baseVariableCandidate.setType(initNode.getType());
                                baseVariableCandidate.isSameMethod = true;
                                map.put(initNode.getVarname(), baseVariableCandidate);
                            }
                        }
                    }
                }
            }
            for (InitNode initNode : ((ClassNode) methodNode.getParent()).getInitNodes()) {
                if (compareType(type, initNode.getType())) {
                    if (!map.containsKey(initNode.getVarname())) {
                        BaseVariableNode baseVariableCandidate = new BaseVariableNode(initNode.getVarname());
                        baseVariableCandidate.setStatementString("this." + initNode.getVarname());
                        baseVariableCandidate.isSameMethod = false;
                        baseVariableCandidate.setType(initNode.getType());
                        map.put(initNode.getVarname(), baseVariableCandidate);
                    }
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    public static Class getClass(String classQualifiedName) {
        try {
            return classLoader.loadClass(classQualifiedName);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
        } catch (IllegalAccessError error) {

        }
        return null;
    }

    public static void main(String[] args) throws MalformedURLException,
            ClassNotFoundException {
////        File customElementsDir = new File("/home/huyenhuyen/Desktop/benmarks/Closure_14/build/classes");
//        File customElementsDir = new File("data/Closure_14/build/classes");
////        File customElementsDir = new File("/home/huyenhuyen/ReRepair/target/classes");
//        URL url = customElementsDir.toURI().toURL();
//        File libFolder = new File("data/Closure_14/build/lib");
//        URL lib = libFolder.toURI().toURL();
//        URL[] urls = new URL[]{url, lib};
//        URLClassLoader loader = new URLClassLoader(urls);

//        Class<?> loadClass = loader.loadClass("AST.parser.ClassA");
        ReflectionHelper.initClassLoader("data/Lang_6/");
        Class clasz = classLoader.loadClass("org.apache.commons.lang3.time.FastDateParser");
//        Class cls = ReflectionHelper.getClass("org.apache.commons.lang3.time.FastDateParser$TextStrategy");
//        System.out.println();
////        Class<?> innerNodeClass = Arrays.stream( clasz.getDeclaredClasses() )
////                .filter( c -> c.getSimpleName().equals("TextStrategy") ).findFirst().get();
////        URL url = classLoader.findResource("org/apache/commons/lang3/time/FastDateParser.class");
////        URLClassLoader loader = new URLClassLoader(new URL[]{url});
//        Class clsss = classLoader.loadClass("org.apache.commons.lang3.time.FastDateParser");
        for (Method c : clasz.getDeclaredMethods()) {
            System.out.println(c.getGenericReturnType() + " - " + c.getName());
            if (c.isSynthetic()) {
                c.setAccessible(true);
                System.out.println(c.getName());
            }
        }
        //        ReflectionHelper.findMethodCalledSameType("com.google.javascript.jscomp.Compiler", "int");
        //case 3 with "..." in param type
//        ReflectionHelper.("com.google.javascript.jscomp.deps.DepsFileParser", "int");
    }


}
