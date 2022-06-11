package main.core;

public class PathCandiString {
    private String candi;
    private int startPos;
    private float score;
    private String scnJavaBackup;
    private String scnTargetJavaFile;

    public String getCandi() {
        return candi;
    }

    public void setCandi(String candi) {
        this.candi = candi;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getScnJavaBackup() {
        return scnJavaBackup;
    }

    public void setScnJavaBackup(String scnJavaBackup) {
        this.scnJavaBackup = scnJavaBackup;
    }

    public String getScnTargetJavaFile() {
        return scnTargetJavaFile;
    }

    public void setScnTargetJavaFile(String scnTargetJavaFile) {
        this.scnTargetJavaFile = scnTargetJavaFile;
    }
}
