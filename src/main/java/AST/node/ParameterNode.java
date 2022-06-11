package AST.node;



public class ParameterNode extends JavaNode {
    protected String type;
    protected boolean isFinal;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    @Override
//    public boolean isFinal() {
//        return isFinal;
//    }

    @Override
    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public ParameterNode() {

    }

    public ParameterNode(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ParameterNode{" +
                "id=" + this.id +
                "type='" + type + '\'' +
                '}';
    }

}
