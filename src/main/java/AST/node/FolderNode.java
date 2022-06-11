package AST.node;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;


public class FolderNode extends Node {

//    @JsonIgnore
//    public List<ClassNode> getClassNodes() {
//        List<ClassNode> result = new ArrayList<>();
//        for (Node child : this.getChildren()) {
//            if (child instanceof ClassNode)
//                result.add((ClassNode) child);
//            else if (child instanceof FolderNode) {
//                result.addAll(((FolderNode) child).getClassNodes());
//            }
//        }
//        return result;
//    }

    @JsonIgnore
    public static List<ClassNode> getClassNodes(Node folderNode) {
        List<ClassNode> result = new ArrayList<>();
        for (Node child : folderNode.getChildren()) {
            if (child instanceof ClassNode) {
                result.add((ClassNode) child);
            } else if (child instanceof EnumNode) {
                result.add((ClassNode) child);
            }
            List<ClassNode> classNodes = getClassNodes(child);
            result.addAll(classNodes);
//            else if (child instanceof FolderNode) {
//                result.addAll(((FolderNode) child).getClassNodes());
//            }
        }
        return result;
    }

    @JsonIgnore
    public ArrayList<Relationship> getClassRelationships() {
        ArrayList<Relationship> relationshipList = new ArrayList<Relationship>();
        List<ClassNode> classes = FolderNode.getClassNodes(this);

        for (ClassNode cd : classes) {
            //lay relationship kieu extend;
            if (cd.getParentClass() != null) {
                int keySuperClass = this.findIdByQualifiedName(cd.getParentClass(), classes);
                if (keySuperClass != -1) {
                    Relationship r = new Relationship();
                    r.setFrom(cd.getId());
                    r.setTo(keySuperClass);
                    r.setRelationship(Relationship.CLASS_EXTENSION);
                    relationshipList.add(r);
                }
            }

            //lay relationship kieu implements;
//            if (cd.getInterfaceList() != null) {
//                for (String s : cd.getInterfaceList()) {
//                    int keySuperInterface = this.findIdByQualifiedName(s, classes);
//                    if (keySuperInterface != -1) {
//                        Relationship r = new Relationship();
//                        r.setFrom(cd.getId());
//                        r.setTo(keySuperInterface);
//                        if (cd.isInterface())
//                            r.setRelationship(Relationship.CLASS_EXTENSION);
//                        else r.setRelationship(Relationship.CLASS_IMPLEMENTATION);
//                        relationshipList.add(r);
//                    }
//                }
//            }
        }
        return relationshipList;
    }

    public int findIdByQualifiedName(String name, List<ClassNode> classes) {
        if (classes.size() > 0) {
            for (ClassNode cd : classes) {
                if (name.equals(cd.getQualifiedName())) return cd.getId();
            }
            return -1;
        }
        return -1;
    }


    public ClassNode findClassByQualifiedName(String className) {
        if (className != null) {
//            String[] justclass = JavaLibraryHelper.getClassName(className);
//            if (justclass != null) {
//                for (ClassNode classNode : FolderNode.getClassNodes(this)) {
//                    for (String name : justclass) {
//                        if (name.equals(classNode.getQualifiedName())) {
//                            return classNode;
//                        } else if (name.contains(classNode.getQualifiedName())) {
////                            System.out.println("here");
//                        }
//                    }
//                }
//            } else {
                for (ClassNode classNode : FolderNode.getClassNodes(this)) {
                    String clName = classNode.getQualifiedName();
                    if (className.equals(clName)) {
                        return classNode;
                    } else if (className.contains(classNode.getQualifiedName())) {
//                        System.out.println("here");
                    }
                }
            }
//        }
        return null;
    }

    public void addTypeForMethodInvocations() {
        for (ClassNode classNode : FolderNode.getClassNodes(this)) {
            //addtype for methodInvocation
//            List<MethodNode> methodNodes = classNode.getMethodList();
//            for (MethodNode methodNode : methodNodes) {
//                methodNode.addTypeForMethodInvocations(this, classNode, methodNode);
//            }
        }
    }


}
