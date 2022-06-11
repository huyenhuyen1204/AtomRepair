package main;

import AST.node.FolderNode;
import AST.stm.abst.StatementNode;
import AST.stm.token.*;
import faultlocalization.objects.SuspiciousPosition;
import main.obj.CandidateElement;
import main.obj.Ranking;
import main.patch.TokenPatch;
import main.core.Genner;
import main.core.PatchCandidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static java.util.Collections.reverseOrder;

//mark number params
//set child in params is Instance of Param
// changes = if == 1 ? statistic
// changes > 2 ? (statistic1 + statistic2 + 1)/number_params
public class Fix {
    public static final Logger logger = LoggerFactory.getLogger(Fix.class);

    public static void fixProcess(List<SuspiciousPosition> ss, FolderNode folderNode, Board board) throws IOException, InterruptedException, TimeoutException {
//        List<PatchCandidate> patchCandidateList = new ArrayList<>();
        SortedSet<String> tokenCandidates = new TreeSet<>(Collections.reverseOrder());
        for (SuspiciousPosition s : ss) {
            Board.SuspCodeNode suspCodeNode = board.parseSuspiciousCode(s, folderNode);
            List<TokenCandidate> tokens = new ArrayList<>();
            if (suspCodeNode != null) {
                tokens = parseTokenCandidates(suspCodeNode.suspCodeAstNode, 0);
                SortedSet<String> tmp =
                        genPatches(suspCodeNode, folderNode, tokens);
                tokenCandidates.addAll(tmp);
//                Map<PatchCandidate, Ranking> ranking = new HashMap<>();
//                List<PatchCandidate> tmp = tokenCandidates;
                float min = 100;
                int rank = 0;
                System.out.println("================== getScore ===================");
                int index = 1;
//                int label1 = -1;
//                int label2= -1;
//                int label3 = -1;
//                int label4 = -1;
                //open
//                tokenCandidates.sort(Comparator.comparing(PatchCandidate::getScore, reverseOrder()));
//                for (PatchCandidate tokenCandidate : tmp) {
//                    System.out.println(index++ + ". " + tokenCandidate.getTargetNode().toString() + " -> " + tokenCandidate.toString().replace(" ", ""));
//
//                    if (min > tokenCandidate.getScore()) {
//                        min = tokenCandidate.getScore();
//                        if (!ranking.containsKey(tokenCandidate)) {
//                            rank++;
//                            ranking.put(tokenCandidate, new Ranking(rank));
//                        }
//                    } else {
//                        if (!ranking.containsKey(tokenCandidate)) {
//                            ranking.put(tokenCandidate, new Ranking(rank));
//                        }
//                    }
//                }
                //open
//                List<PatchCandidate> candies = new ArrayList<>();
////                Limit:
//                int count = 0;
//                int max = -2;
//                for (PatchCandidate patchCandidate : tokenCandidates) {
//                    if (count == 3) {
//                        break;
//                    }
//                    if (max != patchCandidate.getScore()) {
//                        count++;
//                        candies.add(patchCandidate);
//                    }
//                }
//                patchCandidateList.addAll(candies);


//                for (Pattern pattern : Genner.patterns) {
//                    pattern.clearMemory();
//                }
//                patterns.clear();
//                nodeMap.clear();
//                genner.patterns.clear();// redundant
//                for (main.core.token.Token token : main.core.token.Token.generatedTokens) { // try to clear references
//                    token.getCandidates().clear();
//                }
//                for (CountResult result : CountResult.generatedInstances) {
//                    result.statistic.clear();
//                    result.total.clear();
//                }
//                main.core.token.Token.generatedTokens.clear();
//                System.gc();
                System.out.println("Done clear memory.");
            }
        }

//        patchCandidateList.sort(Comparator.comparing(PatchCandidate::getScore, reverseOrder()));
//            Map<PatchCandidate, Ranking> finalList = sortByValue(ranking);
        System.out.println("TOTAL CANDIDATE SIZE: " + tokenCandidates.size());
        if (board.runTest) {
            board.testGeneratedTokenPatches(tokenCandidates);
        }

//        System.out.printf("TOKEN SIZE: " + tokensize);
    }

    private static Map<PatchCandidate, Ranking> sortByValue(Map<PatchCandidate, Ranking> map) {
        List<Map.Entry<PatchCandidate, Ranking>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<Float>) ((Map.Entry<PatchCandidate, Ranking>) (o1)).getValue().getRank())
                        .compareTo(((Map.Entry<PatchCandidate, Ranking>) (o2)).getValue().getRank());
            }
        });

        Map<PatchCandidate, Ranking> result = new LinkedHashMap<>();
        for (Iterator<Map.Entry<PatchCandidate, Ranking>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<PatchCandidate, Ranking> entry = (Map.Entry<PatchCandidate, Ranking>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static String generateJavaFile(TokenPatch patch, String javaCode, String buggyCode) {
        String javaFile = javaCode;
        Collections.sort(patch.getCandidates(), new Comparator<CandidateElement>() {
            @Override
            public int compare(CandidateElement o1, CandidateElement o2) {
                return Integer.compare(o2.getEndPos(), o1.getEndPos());
            }

        });
        for (CandidateElement candidate : patch.getCandidates()) {
            if (!buggyCode.equals(candidate.getCandidate())) {
                javaFile = javaFile.substring(0, candidate.getStartPos()) + candidate.getCandidate() + javaFile.substring(candidate.getEndPos());
            }
        }
        return javaFile;
    }

//    public static List<TokenCandidate> generateTokenCandidates(Board.SuspCodeNode suspCodeNode, FolderNode folderNode) {
//        List<TokenCandidate> tokenCandidates = new ArrayList<>();
//        System.out.println("==========CANDIDATES===========");
//        tokenCandidates = parseTokenCandidates(suspCodeNode.suspCodeAstNode, 0);
//        for (TokenCandidate tokenCandidate : tokenCandidates) {
//            if (suspCodeNode.methodNode != null) {
//                List<PatchCandidate> candidateList = Genner3.generateFixCandidates(tokenCandidate.getTargetNode()
//                        , suspCodeNode.methodNode, folderNode);
//                tokenCandidate.setCandidates(candidateList);
//            } else {
//                logger.error("methodNode == null");
//            }
//        }
//
//        return tokenCandidates;
//    }

    public static SortedSet<String> genPatches(Board.SuspCodeNode suspCodeNode,
                                               FolderNode folderNode,
                                               List<TokenCandidate> tokenCandidates) {
        SortedSet<String> patchCandidates = new TreeSet<>(Collections.reverseOrder());

        System.out.println("==========CANDIDATES===========");
        for (TokenCandidate tokenCandidate : tokenCandidates) {
            if (suspCodeNode.methodNode != null) {
                Genner genner = new Genner();
                SortedSet<String> candidateList = genner.generatePatches(tokenCandidate.getTargetNode()
                        , suspCodeNode.methodNode, folderNode, suspCodeNode);
                patchCandidates.addAll(candidateList);
            } else {
                logger.error("methodNode == null");
            }
        }

        return patchCandidates;
    }

    /**
     * create element Candidate
     *
     * @param statementNode
     * @return
     */
    private static List<TokenCandidate> parseTokenCandidates(StatementNode statementNode,
                                                             int level) {
        List<TokenCandidate> tokenCandidates = new ArrayList<>();
        if (statementNode != null) {
            if (statementNode instanceof Token) {
                boolean isAdd = true;
                // excepts
                if (statementNode instanceof BaseVariableNode) {
                    if (((BaseVariableNode) statementNode).getKeyVar() == null) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
//                    if (!hashes.contains(((Token) statementNode).getHashCode())) {
                    TokenCandidate tokenCandidate = new TokenCandidate(statementNode, level);
                    tokenCandidates.add(tokenCandidate);
//                    hashes.add(((Token) statementNode).getHashCode());
//                    }
                }
            }
            //get child (except QualifierName)
            if (statementNode.getChildren().size() > 0 && !(statementNode instanceof QualifiedNameNode)) {
//                    level = level + 1;
                //invocation is root
                if (!(statementNode instanceof MethodInvocationStmNode)
                        && !(statementNode instanceof InfixExpressionStmNode)) {
                    for (StatementNode child : statementNode.getChildren()) {
                        List<TokenCandidate> tokenCandidateList = parseTokenCandidates(child, level);
                        tokenCandidates.addAll(tokenCandidateList);
                    }
                }
            }
        }
        return tokenCandidates;
    }
}


//    private static List<TokenPatch> synthesisTokenPatch(List<List<TokenCandidate>> tokenCandidates) {
//        List<TokenPatch> tokenSynthesis = new ArrayList<>();
//        List<TokenPatch> tmpSynthesis = new ArrayList<>();
//        TokenPatch init = new TokenPatch();
//        tokenSynthesis.add(init);
//        // not use 0 => for replace node
//        if (tokenCandidates.size() > 0) {
//            for (int i = tokenCandidates.size() - 1; i >= 1; i--) {
//                for (TokenPatch tokenSyn : tokenSynthesis) {
//                    for (Candidate candidate : tokenCandidates.) {
//                        TokenPatch tmpTokenPatch = new TokenPatch(tokenSyn.getCandidates());
//                        tmpTokenPatch.addCandidateList(candidate);
//                        tmpSynthesis.add(tmpTokenPatch);
//                    }
//                }
//                //swap
//                tokenSynthesis.clear();
//                tokenSynthesis.addAll(tmpSynthesis);
//                tmpSynthesis.clear();
//            }
//        }
//        return tokenSynthesis;
//    }
//    private static List<TokenPatch> synthesisTokenPatch(List<TokenCandidate> tokenCandidates) {
//        List<TokenPatch> tokenSynthesis = new ArrayList<>();
//        List<TokenPatch> tmpSynthesis = new ArrayList<>();
//        TokenPatch init = new TokenPatch();
//        tokenSynthesis.add(init);
//        if (tokenCandidates.size() > 0) {
//
//            for (int i = tokenCandidates.size() - 1; i >= 0; i--) {
//                List<Candidate> tokens = tokenCandidates.get(i).getCandidates();
//                if (tokens.size() != 0) {
//                    for (TokenPatch tokenSyn : tokenSynthesis) {
//                        for (Candidate candidate : tokens) {
////                            List<TokenPatch> tmpTokenPatch
//                            TokenPatch tmpTokenPatch = new TokenPatch(tokenSyn.getCandidates());
//                            tmpTokenPatch.addCandidate(candidate);
//                            tmpSynthesis.add(tmpTokenPatch);
//                        }
//                    }
//                    //swap
//                    tokenSynthesis.clear();
//                    tokenSynthesis.addAll(tmpSynthesis);
//                    tmpSynthesis.clear();
//                }
//            }
////            for (Candidate candidate : tokenCandidates.get(0).getCandidates()) {
////                TokenPatch tokenPatch = new TokenPatch();
////                tokenPatch.addCandidate(candidate);
////                tmpSynthesis.add(tokenPatch);
////            }
//
//        }
//        return tokenSynthesis;
//    }


