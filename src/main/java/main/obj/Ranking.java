package main.obj;

public class Ranking {
    private int distance;
    private int tokenChangeSize;
    private int patternLevel;
    private int score;
    private float rank;

    public Ranking(int score) {
        this.score = score;
    }

    public void caculator () {
        this.rank = (float) (distance + tokenChangeSize + patternLevel + score)/4;
    }


    public float getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTokenChangeSize() {
        return tokenChangeSize;
    }

    public void setTokenChangeSize(int tokenChangeSize) {
        this.tokenChangeSize = tokenChangeSize;
    }

    public int getPatternLevel() {
        return patternLevel;
    }

    public void setPatternLevel(int patternLevel) {
        this.patternLevel = patternLevel;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}
