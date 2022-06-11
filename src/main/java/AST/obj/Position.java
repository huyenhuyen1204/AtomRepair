package AST.obj;

public class Position {
    private int startPos;
    private int endPos;

    public Position(int startPos, int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }
}
