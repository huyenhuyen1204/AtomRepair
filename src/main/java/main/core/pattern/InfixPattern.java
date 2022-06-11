package main.core.pattern;

import AST.stm.abst.StatementNode;
import AST.stm.node.OperatorNode;
import AST.stm.token.InfixExpressionStmNode;
import main.core.Genner;
import main.core.PatchCandidate;
import main.core.token.OperatorToken;

import java.util.List;

public class InfixPattern extends Pattern {
    private Pattern subPattern;


    public InfixPattern() {
        super();
    }


    @Override
    public String toString() {
        return computeString(getTargetNode());
    }

    private String computeString(StatementNode stm) {
        String equation = "";
        if (stm != null) {
            equation += stm.cast + stm.getLparen();
            equation += genEquation(stm, subPattern);
            equation += stm.getRparen();
        }
        return equation;
    }

//    public void genCandidates(Genner genner) {
//        if (changes.get(0).getOriginalNode() instanceof OperatorNode) {
//            List<PatchCandidate> patches =
//                    genner.genOperatorCandidates((OperatorToken) changes.get(0).getToken(),
//                            this, genner.getTargetToken());
//            if (patches != null) {
//                this.patchCandidates.addAll(patches);
//            }
//        } else {
//            this.computePatchCandidates(genner);
//        }
//    }

    private String genEquation(StatementNode stm, Pattern pattern) {
        String equation = "";
        boolean equalPattern = false;
        if (pattern != null) {
            if (pattern.getTargetNode() == stm) {
                equalPattern = true;
            }
        }
        if (stm instanceof InfixExpressionStmNode) {
            equation += computeString(((InfixExpressionStmNode) stm).getLeft());
            equation += " " + computeString(((InfixExpressionStmNode) stm).getOperator()) + " ";
            equation += computeString(((InfixExpressionStmNode) stm).getRight());
        } else if (equalPattern) {
            equation += pattern.toString();
        }
//        else if (stm instanceof MethodCalledNode) {
//            equation += stm.toString().split("\\(")[0] + "(";
//            for (StatementNode param: ((MethodCalledNode) stm).getAgurements()) {
//                equation += computeString(param) + ",";
//            }
//            equation.substring(0, equation.length() - 1);
//            equation += ")";
//        }  else if (stm instanceof ClassInstanceCreationNode) {
//            equation += stm.toString().split("\\(")[0] + "(";
//            for (StatementNode param: ((ClassInstanceCreationNode) stm).getArgs()) {
//                equation += computeString(param) + ",";
//            }
//            equation.substring(0, equation.length() - 1);
//            equation += ")";
//        }
        else {
            equation += stm.toString();
        }

        return equation;
    }


    public Pattern getSubPattern() {
        return subPattern;
    }

    public void setSubPattern(Pattern subPattern) {
        this.subPattern = subPattern;
    }
}
