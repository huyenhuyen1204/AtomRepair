package AST.node;

public class Relationship {
    public static final String CLASS_EXTENSION = "extends";
    public static final String CLASS_IMPLEMENTATION = "implements";

    private int from;
    private int to;
    private String relationship;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void printInfor() {
        System.out.println("From: " + this.from + "  To: " + this.to + " Relationship:" + relationship);
    }
}
