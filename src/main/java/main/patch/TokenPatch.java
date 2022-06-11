package main.patch;

import main.obj.CandidateElement;

import java.util.ArrayList;
import java.util.List;

public class TokenPatch extends Patch {
    private List<CandidateElement> candidates;
    private float score = -1;

    public TokenPatch() {
        candidates = new ArrayList<>();
    }

    public TokenPatch(List<CandidateElement> candidates) {
        this.candidates = new ArrayList<>();
        if (candidates.size() > 0) {
            for (CandidateElement candidate : candidates) {
                this.candidates.add(candidate);
                this.score = this.score == -1 ? 1: this.score;
                this.score = this.score * (candidate.getStatisticalScore() + candidate.getDistanceName());
            }
        }
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void addCandidate(CandidateElement candidate) {
        this.candidates.add(candidate);
        this.score = this.score == -1 ? 1: this.score;
        this.score = this.score * candidate.getStatisticalScore();
    }
//    public void addCandidateList(List<TokenCandidate> tokenCandidates) {
//        for (TokenCandidate tokenCandidate: tokenCandidates) {
//            for (Candidate candi : tokenCandidate.getCandidates()) {
//                this.candidates.add(candi);
//                this.score = this.score == -1 ? 1 : this.score;
//                this.score = this.score * candi.getScore();
//            }
//        }
//    }


    public List<CandidateElement> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateElement> candidates) {
        this.candidates = candidates;
    }

}
