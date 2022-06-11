package main.obj;

import AST.stm.abst.StatementNode;
import AST.stm.token.MethodCalledNode;
import main.calculator.BayesSmoothing;
import main.calculator.CaculatorDistance;
import main.core.token.Token;

import java.util.HashMap;

public class CandidateElement {
    private String candidate;
    private int startPos; //start postion for replacing
    private int endPos; //end postion for replacing
    private float statisticalScore;

    public float getDistanceName() {
        return distanceName;
    }

    public void setDistanceName(float distanceName) {
        this.distanceName = distanceName;
    }

    public float getDistanceParam() {
        return distanceParam;
    }

    public void setDistanceParam(float distanceParam) {
        this.distanceParam = distanceParam;
    }

    private float distanceName = 0;
    private float distanceParam = 0;
    private StatementNode bugNode;
    private StatementNode fixNode;
    private HashMap<String, String> inputFeature;

    public CandidateElement(StatementNode targetNode, StatementNode sourceNode, Token.Scope scope) {
        //method: changes className, sub args
        if (targetNode instanceof MethodCalledNode && sourceNode instanceof MethodCalledNode) {
            int paramBug = ((MethodCalledNode) targetNode).getArguments().size();
            int paramFix = ((MethodCalledNode) sourceNode).getArguments().size();
            distanceParam = (float) paramFix / paramBug;
        }
        BayesSmoothing bayesSmoothing = new BayesSmoothing();
        InputFeature inputFeature = new InputFeature();
        this.bugNode = targetNode;
        this.candidate = sourceNode.toString();
        this.startPos = targetNode.getStartPostion();
        this.endPos = targetNode.getEndPostion();
        this.fixNode = sourceNode;
//        if (bugNode instanceof OperatorNode) {
//            distanceName = 1;
//            this.statisticalScore = 1;
//        } else {
        distanceName = CaculatorDistance.caculateDistance(targetNode.toString(), sourceNode.toString());
        HashMap<String, String> input = inputFeature.nodeMap(sourceNode.isSameMethod, targetNode, sourceNode, scope);
//            this.statisticalScore = bayesSmoothing.calculatorScore(inputFeature.nodeMap(sourceNode.isSameMethod, targetNode, sourceNode, scope));
        this.statisticalScore = bayesSmoothing.caculatorStatisticalMAXVer1(input);
        if (sourceNode.toString().equals(" || ")) {
            this.statisticalScore = this.statisticalScore - 0.1f; //rarely happended
        }
        this.inputFeature = input;
//        }
    }

    //Create candidate for MethodInvocation
//    public CandidateElement(String candidate, StatementNode bug, StatementNode fix) {
//        distanceName = CaculatorDistance.caculateDistance(bug.toString(), fix.toString());
//        BayesSmoothing bayesSmoothing = new BayesSmoothing();
//        InputFeature inputFeature = new InputFeature();
//        this.bugNode = bug;
//
//        this.candidate = candidate;
//        this.startPos = bug.getStartPostion();
//        this.endPos = bug.getEndPostion();
//        if (bug.toString().equals(fix.toString())) {
//            this.statisticalScore = 1f;
//        } else {
////            this.score = diffTreeNode(bug, fix);
//            this.statisticalScore = bayesSmoothing.calculatorScore(inputFeature.nodeMap(fix.isSameMethod, bug, fix));
//        }
//    }

//    private float diffTreeNode(List<Candidate> candidates, StatementNode bug, StatementNode fix) {
//        if ()
//    }


//    public CandidateElement(Boolean isSameMethod, StatementNode bug, BaseVariableNode fix) {
//        distanceName = CaculatorDistance.caculateDistance(bug.toString(), fix.getKeyVar());
//        BayesSmoothing bayesSmoothing = new BayesSmoothing();
//        InputFeature inputFeature = new InputFeature();
//        if (isSameMethod) {
//            this.candidate = fix.getKeyVar();
//        } else {
//            this.candidate = "this." + fix.getKeyVar();
//        }
//        this.bugNode = bug;
//        this.startPos = bug.getStartPostion();
//        this.endPos = bug.getEndPostion();
//
////        if (type == Fix.FixType.ELEMENT) {
////            this.score = bayesSmoothing.calculatorScore(inputFeature.elementMap(isSameMethod, bug, fix), distance);
////        } else if (type == Fix.FixType.NODE) {
//        if (bug.toString().equals(fix.getKeyVar())) {
//            this.statisticalScore = 1f;
//        } else {
//            this.statisticalScore = bayesSmoothing.calculatorScore(inputFeature.nodeMap(isSameMethod, bug, fix));
//        }
//    }


    public CandidateElement(String candidate, int startPos, int endPos, float statisticalScore) {
        this.candidate = candidate;
        this.startPos = startPos;
        this.endPos = endPos;
        this.statisticalScore = statisticalScore;
    }


    public float getStatisticalScore() {
        return statisticalScore;
    }

    public void setStatisticalScore(float statisticalScore) {
        this.statisticalScore = statisticalScore;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
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

    public StatementNode getBugNode() {
        return bugNode;
    }

    public void setBugNode(StatementNode bugNode) {
        this.bugNode = bugNode;
    }

    public StatementNode getFixNode() {
        return fixNode;
    }

    public void setFixNode(StatementNode fixNode) {
        this.fixNode = fixNode;
    }

}