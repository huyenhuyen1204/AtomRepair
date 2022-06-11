package AST.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "json_type")
public class Node implements Serializable {

    private static final long serialVersionUID = -1411216676620846129L;

    public static int countId = 0;
    protected int id;
    protected String name;
    protected int startLine;
    protected int endLine;

    protected String absolutePath;

    protected int parentNodeId;
    protected int startPosition;
    protected int endPosition;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static int getCountId() {
        return countId;
    }

    public static void setCountId(int countId) {
        Node.countId = countId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public int getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(int parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    @JsonIgnore
    protected Node parent;

    protected List<Node> children;

    public Node() {
        children = new ArrayList<>();
        countId++;
        this.id = countId;
    }

    public void addChildren(List<Node> children, CompilationUnit cu) {
        for (Node node : children) {
            int lineNumber = cu.getLineNumber(node.getStartPosition());
            node.setStartLine(lineNumber);
            node.setParentNodeId(this.getId());
            node.setParent(this);
            this.children.add(node);
        }
    }

    public void addChildrenFolder(List<? extends Node> children) {
        for (Node node : children) {
            node.setParentNodeId(this.getId());
            node.setParent(this);
            this.children.add(node);
        }
    }

    public void setName(String name) {
        this.name = name;
        if (absolutePath == null) {
            setAbsolutePathByName();
        }
    }

    public void setAbsolutePathByName() {
        if (this.parent != null) {
            setAbsolutePath(this.parent.absolutePath + File.separator + this.name);
        }
    }

    /**
     * Get relative path of node
     *
     * @return relative path of node
     */
    @JsonIgnore
    public String getRelativePath() {
        Node root = getRootNode(this);
        if (root != null) {
            return getAbsolutePath().replace(root.getAbsolutePath() + File.separator, "");
        } else return getAbsolutePath();
    }

    /**
     * Get root node of project which contains given input node
     *
     * @param n
     * @return
     */
    public Node getRootNode(Node n) {
        if (n.getParent() != null) {
            return getRootNode(n.getParent());
        }
        return n;
    }

    /**************************************
     * Use getters, setters instead of direct attribute assignment
     * for following methods (due to these methods are not
     * overridden by derive Decorator)
     ***************************************/

    @JsonIgnore
    public List<Node> getAllChildren() {
        return doGetAllChildren(this);
    }

    private List<Node> doGetAllChildren(Node rootNode) {
        List<Node> allChildren = new ArrayList<>();
        for (Node child : rootNode.getChildren()) {
            allChildren.add(child);
            allChildren.addAll(doGetAllChildren(child));
        }
        return allChildren;
    }

    /******
     *Get Type of Node
     *
     *
     */
    //protected string type;
    @JsonIgnore
    public String getObjectType() {
        Class<?> enclosingClass = this.getClass().getEnclosingClass();
        String nodeClass = enclosingClass != null ? enclosingClass.getSimpleName() : this.getClass().getSimpleName();
        return nodeClass;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                '}';
    }
}
