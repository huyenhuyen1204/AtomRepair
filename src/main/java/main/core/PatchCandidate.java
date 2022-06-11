package main.core;

import AST.stm.abst.StatementNode;
import AST.stm.node.OperatorNode;
import AST.stm.token.ClassInstanceCreationNode;
import AST.stm.token.InfixExpressionStmNode;
import AST.stm.token.MethodCalledNode;
import main.Board;
import main.core.pattern.BasePattern;
import main.core.pattern.InfixPattern;
import main.core.pattern.MethodPattern;
import main.core.pattern.Pattern;
import main.core.token.Token;
import main.obj.CandidateElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatchCandidate {

    // changesMap: a token node is subhstituted by the statement source node
    private Map<Integer, StatementNode> changesMap = null;
//    private Pattern pattern;
    private String content = null;
    private StatementNode targetNode;
    private Board.SuspCodeNode suspiciousCode;
//    private List<CandidateElement> candidateElements;
    private float score = -1;
    private float patternLevel = -1;
    private float tokenchangedSize = -1;

    public PatchCandidate(StatementNode targetNode) {
        changesMap = new HashMap<>();
        this.targetNode = targetNode;
    }

    public void clearMemory() {
//        this.candidateElements.clear();
        this.changesMap.clear();
        this.content = null;
        this.suspiciousCode = null;
        this.targetNode = null;
    }

//    public Pattern getPattern() {
//        return pattern;
//    }
//
//    public void setPattern(Pattern pattern) {
//        this.pattern = pattern;
//    }

    public float getDistanceScore() {
        return distanceScore;
    }

    public void setDistanceScore(float distanceScore) {
        this.distanceScore = distanceScore;
    }

    private float distanceScore = -1;


    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Map<Integer, StatementNode> getChangesMap() {
        return changesMap;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public String computeContent(Pattern pattern) { // compute content of a patch according to the "changes" map
        String builder;
        if (pattern instanceof InfixPattern) {
            this.content = computeString(pattern.getTargetNode(), ((InfixPattern) pattern).getSubPattern());
            return this.content;
        } else if (pattern instanceof BasePattern) {
            StatementNode originalElement = pattern.getChanges().get(0).getOriginalNode();
            String prefix = originalElement.getPrefix();
            prefix = prefix.equals("") ? prefix : prefix + ".";
            builder =  prefix;

            Token token = pattern.getChanges().get(0).getToken();
            StatementNode candidate = changesMap.get(token.hashCode());
            if (candidate != null) {
//                if (candidate instanceof MethodCalledNode) {
//                   builder += candidate.toString();
//                } else {
                    String strnew = candidate.toString();
                   builder += strnew;
//                }
            }

            if (token.getTargetScope() == Token.Scope.ONLY_CURRENT) { // need to get suffix
                String suffix = originalElement.getSuffix();
//                suffix =  ? suffix : ;
                if (!suffix.equals("")) {
                    suffix = "." + suffix;
                }
                builder += suffix;
            }
        } else if (pattern instanceof MethodPattern) { // changed element is a method call node
            MethodPattern methodPattern = (MethodPattern) pattern;
            String prefix = methodPattern.getChangedMethod().getPrefix();
            prefix = prefix.equals("") ? prefix : prefix + ".";
            builder = prefix;
            List<StatementNode> argStatementNodes = new ArrayList<>();
            if (methodPattern.getChangedMethod() instanceof MethodCalledNode) {
                String tmpStr = (((MethodCalledNode) methodPattern.getChangedMethod()).getMethodName()) +"(";
                builder = builder + tmpStr;
                argStatementNodes = ((MethodCalledNode) methodPattern.getChangedMethod()).getArguments();

            } else if (methodPattern.getChangedMethod() instanceof ClassInstanceCreationNode) {
                String str = ((ClassInstanceCreationNode) methodPattern
                        .getChangedMethod()).getName() + "(";
              builder = builder + str;
                argStatementNodes = ((ClassInstanceCreationNode) methodPattern.getChangedMethod()).getArgs();
            }
            for (int i = 0; i < argStatementNodes.size(); i++) {
                StatementNode argNode = argStatementNodes.get(i);
                Pattern argPattern = methodPattern.getChangedArgsMap().get(argNode);

                if (argPattern != null) {
                    String newStr = computeContent(argPattern);
                    builder += newStr;
                } else {
                    String newStr = argNode.toString();
                    builder += newStr;

//                    if (argNode instanceof Token) {
//                        builder.append(argNode.toString());
//                    } else {
//                        builder.append(argNode.getStatementString());
//                    }
                }
                int leng = argStatementNodes.size() - 1;
                if (i < leng) {
                    builder += ",";
                }
            }
            builder += ")";

            String suffix = methodPattern.getChangedMethod().getSuffix();
//            suffix = suffix.equals("") ? suffix : "." + suffix;
            if (!suffix.equals("")) {
                suffix = "." + suffix;
            }
            builder += suffix;
        } else {
            builder = "";
        }
        content = builder;

        return content;
    }


    public void setChangesMap(Map<Integer, StatementNode> changesMap) {
        this.changesMap = changesMap;
    }

    @Override
    public String toString() {
        return this.content;
    }

    public StatementNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(StatementNode targetNode) {
        this.targetNode = targetNode;
    }

    public double calculateScore(Pattern pattern) {
//        candidateElements = new ArrayList<>();
        this.patternLevel = pattern.getPatternLevel();
        this.tokenchangedSize = pattern.getChanges().size();

        if (pattern instanceof InfixPattern) {
            if (!(pattern.getChanges().get(0).getOriginalNode() instanceof OperatorNode)) {
                StatementNode targetNode = pattern.getChanges().get(0).getOriginalNode();
                StatementNode sourceNode = changesMap.get(pattern.getChanges().get(0).getTokenKey());
                CandidateElement candidateElement = new CandidateElement(targetNode, sourceNode, pattern.getChanges().get(0).getToken().getTargetScope() );
//                candidateElements.add(candidateElement);
                score = score == -1 ? candidateElement.getStatisticalScore() : score + candidateElement.getStatisticalScore();
                distanceScore = distanceScore == -1 ? candidateElement.getDistanceName() : distanceScore + candidateElement.getDistanceName();
            } else {
                score = 1f;
                distanceScore = 7;
            }
        } else if (pattern instanceof BasePattern) {
            StatementNode targetNode = pattern.getChanges().get(0).getOriginalNode();
            StatementNode sourceNode = changesMap.get(pattern.getChanges().get(0).getTokenKey());
            CandidateElement candidateElement = new CandidateElement(targetNode, sourceNode,
                    pattern.getChanges().get(0).getToken().getTargetScope());
//            candidateElements.add(candidateElement);
            score = score == -1 ? candidateElement.getStatisticalScore() : score + candidateElement.getStatisticalScore();
            distanceScore = distanceScore == -1 ? candidateElement.getDistanceName() : distanceScore + candidateElement.getDistanceName();
        } else if (pattern instanceof MethodPattern) {
            int paramSize = -1;
            for (Change change : pattern.getChanges()) {
                StatementNode targetNode = change.getOriginalNode();
                StatementNode sourceNode = changesMap.get(change.getTokenKey());
                CandidateElement candidateElement = new CandidateElement(targetNode, sourceNode,change.getToken().getTargetScope());
//                candidateElements.add(candidateElement);

                score = score == -1 ? candidateElement.getStatisticalScore() : score + candidateElement.getStatisticalScore();
                distanceScore = distanceScore == -1 ? candidateElement.getDistanceName() : distanceScore + candidateElement.getDistanceName();
                //set level
                this.patternLevel = pattern.getPatternLevel();
                if (targetNode.paramSize != null) {
                    paramSize = targetNode.paramSize;
                }
            }
            if (paramSize != -1) {
                score = score / pattern.getChanges().size();
                distanceScore = distanceScore / pattern.getChanges().size();
            }
        }
        return (double) Math.floor(score * 100000) / 100000;
    }

    private String computeString(StatementNode stm, Pattern subPattern) {
        String equation = "";
        if (stm != null) {
            equation += stm.cast + stm.getLparen();
            equation += genEquation(stm, subPattern);
            equation += stm.getRparen();
        }
        return equation;
    }

    private String genEquation(StatementNode stm, Pattern pattern) {
        String equation = "";
        boolean equalPattern = false;
        if (pattern != null) {
            if (pattern.getTargetNode() == stm) {
                equalPattern = true;
            }
        }

        if (equalPattern) {
            equation += computeContent(pattern);
        } else if (stm instanceof InfixExpressionStmNode) {
            equation += computeString(((InfixExpressionStmNode) stm).getLeft(), pattern);
            equation += " " + computeString(((InfixExpressionStmNode) stm).getOperator(), pattern) + " ";
            equation += computeString(((InfixExpressionStmNode) stm).getRight(), pattern);
        } else {
            equation += stm.toString();
        }
        return equation;
    }

    public float getPatternLevel() {
        return patternLevel;
    }

    public void setPatternLevel(float patternLevel) {
        this.patternLevel = patternLevel;
    }

    public float getTokenchangedSize() {
        return tokenchangedSize;
    }

    public Board.SuspCodeNode getSuspiciousCode() {
        return suspiciousCode;
    }

    public void setSuspiciousCode(Board.SuspCodeNode suspiciousCode) {
        this.suspiciousCode = suspiciousCode;
    }
}
