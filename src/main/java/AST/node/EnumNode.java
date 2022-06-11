package AST.node;

import java.util.ArrayList;
import java.util.List;

public class EnumNode extends ClassNode{
    private List<String> enumConstants;
    public EnumNode() {
        super();
        enumConstants = new ArrayList<>();
    }
    public void addEnumConstant (String enumConstant) {
        enumConstants.add(enumConstant);
    }

    public List<String> getEnumConstants() {
        return enumConstants;
    }

    public void setEnumConstants(List<String> enumConstants) {
        this.enumConstants = enumConstants;
    }


}
