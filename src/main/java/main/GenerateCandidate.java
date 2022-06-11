//package main;
//
//import AST.node.*;
//import AST.stm.abst.StatementNode;
//import AST.stm.token.ClassInstanceCreationNode;
//import AST.stm.token.*;
//import AST.stm.nodetype.*;
//import main.calculator.CaculatorDistance;
//import main.obj.CandidateElement;
//import main.object.Candidate;
//import main.token.PatchCandidate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import util.JavaLibraryHelper;
//import util.OSHelper;
//
//import java.util.*;
//
///**
// * generate candidate possible
// */
//public class GenerateCandidate {
//
//    public static final Logger logger = LoggerFactory.getLogger(GenerateCandidate.class);
//    public static final int DEEP_LEVEL = 1;
//    //    public static int DEEP_AUTO = 0;
//    public static final int MAX_TOKEN = 30;
//    //    public static final float MAX_DISTANCE
//    private static StatementNode bugNode;
//    private static List<Integer> hashBugElement;
//    private static List<StatementNode> elementsOfBugNode;
//    private static List<Integer> done = new ArrayList<>(); //parsed
//    private static List<Integer> hashes = new ArrayList<>(); //exist in candidate list
//
//
//    public static List<TokenCandidate> generateTokenCandidates(Board.SuspCodeNode suspCodeNode, FolderNode folderNode) {
//        List<TokenCandidate> tokenCandidates = new ArrayList<>();
//        elementsOfBugNode = new ArrayList<>();
//        System.out.println("==========CANDIDATES===========");
//        tokenCandidates = parseTokenCandidates(elementsOfBugNode, suspCodeNode.suspCodeAstNode, 0);
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
//
//    private static List<CandidateElement> convertPatchToCandidate(List<PatchCandidate> candidateList) {
//        List<CandidateElement> candidates = new ArrayList<>();
//        for (PatchCandidate patchCandidate : candidateList) {
//            CandidateElement candidate = new CandidateElement(patchCandidate.toString(), -1, -1, -1l);
//            candidates.add(candidate);
//        }
//        return candidates;
//    }
//
////    private static void generateCandidate(TokenCandidate tokenCandidate, MethodNode methodNode, ClassNode classNode, FolderNode folderNode) {
////        if (tokenCandidate.getTargetNode() instanceof InfixExpressionStmNode) {
////            HashMap<Integer, Candidate> candidateHashMap = generateInfixExpression((InfixExpressionStmNode) tokenCandidate.getTargetNode());
////            tokenCandidate.setTokenNode(candidateHashMap);
////        } else if (tokenCandidate.getTargetNode() instanceof QualifiedNameNode) {
////            QualifiedNameNode qualifiedNameNode = (QualifiedNameNode) tokenCandidate.getTargetNode();
////            if (qualifiedNameNode.getParent() == null) {
////                if (qualifiedNameNode.getQualifier().getType() != null) {
////                    // Generate Candidate
////                    ClassNode clazz = folderNode.findClassByQualifiedName(qualifiedNameNode.getQualifier().getType());
////                    HashMap<Integer, Candidate> candidateHashMap = generateQualifier(qualifiedNameNode.getQualifier().getKeyVar(), qualifiedNameNode.getName().getType(), clazz, tokenCandidate.getTargetNode());
////                    tokenCandidate.setTokenNode(candidateHashMap);
////                } else {
////                    // Find candidate
////                    HashMap<Integer, Candidate> candidateHashMap = findQualifier(qualifiedNameNode.getQualifier().getType(), methodNode, classNode, tokenCandidate.getTargetNode());
////                    tokenCandidate.setTokenNode(candidateHashMap);
////                }
////            } else {
////                System.out.println("Chuwa xu ly: qualifiedNameNode.getParent() != null");
////            }
////
////        } else if (tokenCandidate.getTargetNode() instanceof MethodCalledNode) {
////            //REPLACE a Token MethodCalled
////            //MethodCalledNode.type != null
////            if (((Token) tokenCandidate.getTargetNode()).getType() != null) {
////                ClassNode clazz = folderNode.findClassByQualifiedName(((Token) ((MethodCalledNode) tokenCandidate.getTargetNode()).getParent()).getType());
////                if (clazz != null) {
////                    //Generate Candidate (sameType)
////                    HashMap<Integer, Candidate> candidates = generateMethodCalled((Token) tokenCandidate.getTargetNode(), clazz);
////                    tokenCandidate.setTokenNode(candidates);
////                } else {
////                    logger.error("Chua xu ly clazz == null");
////                }
////            } else {
////                //MethodCalled.type == null
////                // visit in Source code & find candidate (the same Parent & methodCalled)
////                HashMap<Integer, Candidate> candidates = findMethodCalledCandidate((Token) tokenCandidate.getTargetNode(), methodNode, classNode);
////                tokenCandidate.setSourceNode(candidates);
////            }
////        } else {
////            //edit : find with node parent or not!
////            // === CASE: BaseVariableNode, BooleanNode, MethodInvocationStmNode ===
////            // ------ REPLACE a Token (BaseVar) ----
//////            List<Candidate> candidates = new ArrayList<>();
////            if (((Token) tokenCandidate.getTargetNode()).getType() != null) {
////                //field candidate in Method -- BaseVar
////                HashMap<Integer, Candidate> candidateInMethod = findInInitNodes(methodNode.getInitNodes(), tokenCandidate.getTargetNode(), false);
////                tokenCandidate.addTokenNode(candidateInMethod);
////                //field candidate in field -- BaseVar
////                HashMap<Integer, Candidate> candidateFields = findInInitNodes(classNode.getInitNodes(), tokenCandidate.getTargetNode(), true);
////                tokenCandidate.addTokenNode(candidateFields);
////            }
////            // ------------ END REPLACE a Token ------------
////            // -------------- REPLACE an OTHER NODE ---------------
////            // replace targetNode with SourceNode the same type in FILE. token != BaseVar
//////            HashMap<Integer, Candidate> candidateList = findCandidateSameType((Token) tokenCandidate.getTargetNode(), methodNode, classNode);
//////            tokenCandidate.setSourceNode(candidateList);
////            // ---------- END REPLACE a NODE ----------
////        }
////    }
//
//    private static List<CandidateElement> generateCandidate(
//            StatementNode targetNode, MethodNode methodNode, FolderNode folderNode) {
//        done = new ArrayList<>();
//        hashes = new ArrayList<>();
//        HashMap<Integer, CandidateElement> candidateHashMap = new HashMap<>();
//
//        //TODO
//        //1
//        HashMap<Integer, CandidateElement> genCandidates = generateCandidateNodes(
//                targetNode, folderNode);
//
//        candidateHashMap.putAll(genCandidates);
//        if (!(targetNode instanceof InfixExpressionStmNode)) {
////            if (!(targetNode instanceof MethodCalledNode)) {
//            //2
//            HashMap<Integer, CandidateElement> candidateInits = findBaseVarNode(targetNode.getType(), ((StatementNode) targetNode), methodNode, ((StatementNode) targetNode).getLine());
//            candidateHashMap.putAll(candidateInits);
////            }
//            //3
//            HashMap<Integer, CandidateElement> candidatesNodes = findSourceNodes(targetNode, methodNode, folderNode);
//            candidateHashMap.putAll(candidatesNodes);
//        }
//
//        List<CandidateElement> candidateList = JavaLibraryHelper.convertHasmapToList(candidateHashMap);
//
//        return candidateList;
//    }
//
//    private static HashMap<Integer, CandidateElement> generateCandidateNodes(StatementNode token,
//                                                                      FolderNode folderNode) {
//        HashMap<Integer, CandidateElement> candidates = new HashMap<>();
//        if (token instanceof QualifiedNameNode) {
////            QualifiedNameNode qualifiedNameNode = (QualifiedNameNode) token;
////            if (qualifiedNameNode.getParent() == null) {
////                // Generate Candidate
////                ClassNode clazz = folderNode.findClassByQualifiedName(qualifiedNameNode.getQualifier().getType());
////                if (clazz != null) {
////                    HashMap<Integer, Candidate> candidateHashMap = generateQualifier(qualifiedNameNode.getQualifier().getKeyVar(), qualifiedNameNode.getName().getType(), clazz, (StatementNode) token);
////                    candidates.putAll(candidateHashMap);
////                }
////            } else {
////                System.out.println("Chuwa xu ly: qualifiedNameNode.getParent() != null");
////            }
//        } else if (token instanceof MethodCalledNode) {
////            //REPLACE a MethodCalled
////            //MethodCalledNode.type != null
////            if (token.getType() != null) {
////                ClassNode clazz = folderNode.findClassByQualifiedName((token.getParent()).getType());
////                if (clazz != null) {
////                    //Generate Candidate (sameType)
////                    HashMap<Integer, Candidate> candidateList = generateMethodCalled(isSameMethod, token, clazz);
////                    candidates.putAll(candidateList);
////                } else {
////                    logger.error("Chua xu ly clazz == null");
////                }
////            }
//        } else if (token instanceof InfixExpressionStmNode) {
////            HashMap<Integer, Candidate> candidateHashMap = generateInfixExpression((InfixExpressionStmNode) token);
////            candidates.putAll(candidateHashMap);
//        } else if (token instanceof BooleanNode) {
////            generateBooleanCandidate(token, candidates);
//        }
//        return candidates;
//    }
//
//    private static HashMap<Integer, CandidateElement> findQualifier(String qualifierType, MethodNode method, ClassNode classNode, StatementNode targetNode) {
//        HashMap<Integer, CandidateElement> map = new HashMap<>();
//        for (MethodNode methodNode : classNode.getMethodList()) {
//            if (method.hashCode() == methodNode.hashCode()) {
//                findQualifierCandidate(map, methodNode.getStatementNodes(), qualifierType, targetNode, true);
//            } else {
//                findQualifierCandidate(map, methodNode.getStatementNodes(), qualifierType, targetNode, false);
//            }
//        }
//        return map;
//    }
//
//    private static void findQualifierCandidate(HashMap<Integer, CandidateElement> map, List<StatementNode> statements, String qualifierType, StatementNode targetNode, boolean isSameMethod) {
//        for (StatementNode stm : statements) {
//            if (stm instanceof Token) {
//                if (stm instanceof QualifiedNameNode) {
////                    QualifiedNameNode qualifiedNameNode = (QualifiedNameNode) stm;
////                    if (qualifiedNameNode.getQualifier().getType().equals(qualifierType)) {
////                        if (!map.containsKey(qualifiedNameNode.getHashCode())) {
////                            Candidate candidate = new Candidate(targetNode, stm);
////                            map.put(qualifiedNameNode.getHashCode(), candidate);
////                        }
////                    }
//                }
//            }
//            if (stm != null) {
//                if (stm.getChildren().size() > 0) {
//                    findQualifierCandidate(map, stm.getChildren(), qualifierType, targetNode, isSameMethod);
//                }
//            }
//        }
//    }
//
////    private static HashMap<Integer, Candidate> generateQualifier(String qualifier, String type, ClassNode clazz, StatementNode targetNode) {
////        HashMap<Integer, Candidate> map = new HashMap<>();
////        List<FieldNode> fieldNodes = clazz.getFieldList();
////        for (FieldNode fieldNode : fieldNodes) {
////            if (fieldNode.getType().equals(type) &&
////                    !fieldNode.getVisibility().equals("private")) {
////                String format = qualifier + "." + fieldNode.getName();
////                float distance = CaculatorDistance.caculatorDistence(targetNode.getStatementString(), format);
////                Candidate candidate = new Candidate(format, targetNode.getStartPostion(), targetNode.getEndPostion(), distance);
////                map.put(format.hashCode(), candidate);
////            }
////        }
////        return map;
////    }
//
////    private static List<Candidate> generateInfixExpression(InfixExpressionStmNode targetNode) {
////        List<Candidate> candidates = new ArrayList<>();
////        List<String> infix1 = Arrays.asList(">", ">=", "<", "<=");
////        List<String> infix2 = Arrays.asList("!", "");
////        List<String> infix3 = Arrays.asList("==", "!=");
////        List<String> infix4 = Arrays.asList("&&", "||");
////        if (infix1.contains(targetNode.getOperator())) {
////            generateOperator(infix1, targetNode, candidates);
////        } else if (infix2.contains(targetNode.getOperator())) {
////            generateOperator(infix2, targetNode, candidates);
////        } else if (infix3.contains(targetNode.getOperator())) {
////            generateOperator(infix3, targetNode, candidates);
////        } else if (infix4.contains(targetNode.getOperator())) {
////            generateOperator(infix4, targetNode, candidates);
////        }
////        return candidates;
////    }
//
//    private void replaceNode() {
//        //if instance of basevar
//        // find basevar
//        // find other sameType (if hash parent find parent sameType)
//        // find methodInvo sameType
//        // List<Candidate> -> add STMNode
//        // if instance of MethodCalled
//        // find parent sameType, if has child, find child sameType
//        // find params & replace param or remove
//        // List <StatementNode> -> add STMNode
//        // for (Param: param) List<STMNode>
//        // else if MethodInvocation
//        // genChild
//    }
//
//
//    private static void generateOperator(List<String> infix1, InfixExpressionStmNode targetNode, List<Candidate> candidates) {
//        for (String operator : infix1) {
//            if (!targetNode.getOperator().equals(operator)) {
//                if (!operator.equals("!") && !operator.equals("")) {
//                    operator = " " + operator + " ";
//                }
//                Candidate candidate = new Candidate(operator, targetNode.getStartPostion(), targetNode.getEndPostion(), 1);
//                candidates.add(candidate);
//            }
//        }
//    }
//
//    /**
//     * replace targetToken with SourceToken the same type in FILE. token != BaseVar
//     *
//     * @param targetNode is token node
//     * @return
//     */
//    private static HashMap<Integer, CandidateElement> findSourceNodes(StatementNode targetNode,
//                                                               MethodNode methodNode, FolderNode folderNode) {
//        HashMap<Integer, CandidateElement> candidates = new HashMap<>();
//        List<StatementNode> srcNodes = new ArrayList<>();
//
////        //Change params of method
////        if (targetNode instanceof MethodCalledNode) {
////            HashMap<Integer, Candidate> candidateHashMap
////                    = generateCandidatesWithParams((MethodCalledNode) targetNode,
////                    targetNode, methodNode, classNode, folderNode);
////           candidates.putAll(candidateHashMap);
////        }
//
////        if (((Token) targetNode).getHashCode() == ((Token) bugNode).getHashCode()) {
////            if (targetNode instanceof MethodCalledNode
////                    || targetNode instanceof MethodInvocationStmNode) {
////                hashes.add(((Token) targetNode).getHashCode());
////                done.add(((Token) targetNode).getHashCode());
////                srcNodes.add(targetNode);
////            }
////        }
//        if (targetNode == bugNode) {
//            srcNodes.add(targetNode);
//        }
//        // === find source node in method bug ===
//        List<StatementNode> srcNodesSameMethod =
//                findSourceNodesInMethod(hashes, methodNode, targetNode, true);
//        srcNodes.addAll(srcNodesSameMethod);
//
//        // === find sources have same type in a Class ===
//        for (MethodNode method : ((ClassNode) methodNode.getParent()).getMethodList()) {
//            if (method.hashCode() != methodNode.hashCode()) {
//                List<StatementNode> srcNodesDiffMethod = findSourceNodesInMethod(hashes, method, targetNode, false);
//                srcNodes.addAll(srcNodesDiffMethod);
//            }
//        }
//
//        // === Ranking score & limit 30
//        List<CandidateElement> candidatesTemp = new ArrayList<>();
//        for (StatementNode srcNode : srcNodes) {
//            CandidateElement candidate = new CandidateElement(targetNode, srcNode);
//            candidatesTemp.add(candidate);
//        }
//        subCandidateList(candidatesTemp);
//
//        //generate candidate from source node
//        for (CandidateElement source : candidatesTemp) {
//            if (!done.contains(((Token) source.getFixNode()).getHashCode())) {
//                HashMap<Integer, CandidateElement> candidateHashMap = replaceSourceNode(
//                        source.getFixNode(),
//                        targetNode, methodNode, folderNode);
//                candidates.putAll(candidateHashMap);
//                done.add(((Token) source.getFixNode()).getHashCode());
//            }
//        }
//        // end limit
//        return candidates;
//    }
//
//    private static List<StatementNode> findSourceNodesInMethod(List<Integer> hashes,
//                                                               MethodNode methodNode,
//                                                               StatementNode targetNode,
//                                                               boolean isSameMethod) {
//        List<StatementNode> tokens = new ArrayList<>();
//        for (StatementNode stm : methodNode.getStatementNodes()) {
//            List<StatementNode> tokenList = findSourceCandidate(hashes, stm, targetNode, isSameMethod);
//            tokens.addAll(tokenList);
//        }
//        return tokens;
//    }
//
//    private static HashMap<Integer, CandidateElement> replaceSourceNode(
//            StatementNode srcNode, StatementNode targetNode,
//            MethodNode methodNode, FolderNode folderNode) {
//
//        HashMap<Integer, CandidateElement> candidates = new HashMap<>();
//        if (!(srcNode instanceof InfixExpressionStmNode)
//                && !(srcNode instanceof BooleanNode)) {
//            //Invocation
//            if (srcNode instanceof MethodInvocationStmNode) {
//
//                MethodInvocationStmNode methodInvocationStmNode = (MethodInvocationStmNode) srcNode;
//
//                List<List<CandidateElement>> mapUnitCandidates = generateMethodInvocationCandidate(methodInvocationStmNode.getChildren().get(0),
//                        methodNode, targetNode, folderNode);
//
//                List<List<CandidateElement>> candies = synthesis(mapUnitCandidates, targetNode);
//                for (List<CandidateElement> combo : candies) {
//                    CandidateElement methodInvo = formatMethodInvoCandidate(
//                            combo, (MethodInvocationStmNode) srcNode,
//                            targetNode);
//                    addCandidate(candidates, methodInvo);
//                }
//            } else if (srcNode instanceof MethodCalledNode) {
//
//                if (targetNode instanceof MethodCalledNode) {
//                    HashMap<Integer, CandidateElement> candidateHashMap =
//                            generateCandidatesWithParams((MethodCalledNode) srcNode,
//                                    targetNode, methodNode, folderNode);
//                    candidates.putAll(candidateHashMap);
//                }
//            } else {
//                CandidateElement candidate = new CandidateElement(targetNode,
//                        (StatementNode) srcNode);
//                addCandidate(candidates, candidate);
//            }
//        }
//        return candidates;
//    }
//
//    private static CandidateElement formatMethodInvoCandidate
//            (List<CandidateElement> combo, MethodInvocationStmNode sourceNode,
//             StatementNode targetNode) {
//        CandidateElement candidate = null;
//        String methodInvo = "";
//        float p = 0;
////        boolean isBugNode = false;
////        if (elementsOfBugNode.contains(targetNode)) {
////            if (targetNode instanceof MethodCalledNode
////                    || targetNode instanceof MethodInvocationStmNode) {
////                isBugNode = true;
////            }
////        }
////        if (targetNode.hashCode() == bugNode.hashCode()) {
//        for (CandidateElement element : combo) {
//            methodInvo += element.getCandidate() + ".";
////            if (isBugNode) {
////                p = p == 0 ? 1 : p;
////                p = p * element.getScore();
////            }
//        }
//        if (!methodInvo.equals("")) {
//            methodInvo = methodInvo.substring(0, methodInvo.length() - 1);
//            candidate = new CandidateElement(methodInvo, targetNode, sourceNode);
////            if (isBugNode) {
////                candidate.setScore(p);
////            }
//        }
////        } else {
////            for (Candidate element : combo) {
//////            p = p == 0 ? 1 : p;
////                methodInvo += element.getCandidate() + ".";
//////            p = p * element.getScore();
////            }
////            if (!methodInvo.equals("")) {
////                methodInvo = methodInvo.substring(0, methodInvo.length() - 1);
////                candidate = new Candidate(isSameMethod, methodInvo, targetNode, sourceNode);
////            }
////        }
//        return candidate;
//    }
//
//    private static CandidateElement generateBooleanCandidate(StatementNode targetNode) {
////        List<Candidate> candidates = new ArrayList<>();
//        if (((BooleanNode) targetNode).isValue()) {
//            CandidateElement candidateFalse = new CandidateElement("false", ((StatementNode) targetNode).getStartPostion(),
//                    ((StatementNode) targetNode).getEndPostion(), 1);
//            return candidateFalse;
////        addCandidate(candidates, candidateFalse);
//        } else {
//            CandidateElement candidateTrue = new CandidateElement("true", ((StatementNode) targetNode).getStartPostion(),
//                    ((StatementNode) targetNode).getEndPostion(), 1);
//            return candidateTrue;
////        addCandidate(candidates, candidateTrue);
//        }
//    }
//
//    private static List<List<CandidateElement>> generateMethodInvocationCandidate(
//            StatementNode statementNode, MethodNode methodNode,
//            StatementNode targetNode, FolderNode folderNode) {
//        List<List<CandidateElement>> candidates = new ArrayList<>();
//        if (statementNode != null) {
//            //basevar
//            if (statementNode instanceof BaseVariableNode) { //base var
////                if (((BaseVariableNode) statementNode).getKeyVar() != null) {
////                    Candidate candidate = new Candidate(((BaseVariableNode) statementNode).getKeyVar(),
////                            statementNode.getStartPostion(), statementNode.getEndPostion(), 1);
////                    HashMap<Integer, Candidate> innits = findBaseVarNode(statementNode.getType(), statementNode, methodNode, classNode
////                            , statementNode.getLine());
////                    List<Candidate> cds = JavaLibraryHelper.convertHasmapToList(innits);
////                    cds.add(candidate);
////                    candidates.add(cds);
////                }
//                if (((BaseVariableNode) statementNode).getKeyVar() != null) {
//                    List<Candidate> basevarCandidates = generateCandidate(statementNode, methodNode, folderNode);
//                    candidates.add(basevarCandidates);
//                }
//            } else if (statementNode instanceof MethodCalledNode) { //methodCall
//                //find params
//                HashMap<Integer, Candidate> methodCandies = generateCandidatesWithParams(
//                        (MethodCalledNode) statementNode,
//                        targetNode, methodNode, folderNode);
//                List<Candidate> candidateList = JavaLibraryHelper.convertHasmapToList(methodCandies);
//                candidates.add(candidateList);
//            } else {
//                // giu nguyen
//                Candidate candidate = new Candidate(statementNode.getStatementString(),
//                        statementNode.getStartPostion(), statementNode.getEndPostion(), 1);
//                List<Candidate> candidateList = new ArrayList<>();
//                candidateList.add(candidate);
//                candidates.add(candidateList);
//            }
//            if (statementNode.getChildren().size() > 0) {
//                List<List<Candidate>> cds = generateMethodInvocationCandidate(
//                        statementNode.getChildren().get(0), methodNode, targetNode, folderNode);
//                candidates.addAll(cds);
//            }
//        }
//        return candidates;
//    }
//
////    private static List<Candidate> synthesizeCandidates(StatementNode stmNode,
////                                                     HashMap<Integer, List<Candidate>> mapUnitCandidates) {
////        List<Candidate> synthCandidates = new ArrayList<>();
////        boolean isSynthe = true;
////        if (stmNode instanceof BaseVariableNode) {
////            if (((BaseVariableNode) stmNode).getKeyVar() == null) {
////                isSynthe = false;
////            }
////        }
////        if (isSynthe) {
////            List<Candidate> currNodeCandidates = mapUnitCandidates.get(stmNode.hashCode());
////            if (currNodeCandidates == null) {
////                System.out.println("stmNode not found in the map");
////                synthCandidates.add(stmNode.getStatementString());
////                return synthCandidates;
////            }
////            List<String> suffixCandidates;
////            if (stmNode.getChildren().size() == 1) {
////                suffixCandidates = synthesizeCandidates(stmNode.getChildren().get(0),
////                        mapUnitCandidates); // assume that currNode has only one child
////                for (Candidate currNodeCand : currNodeCandidates) {
////                    for (String suffix : suffixCandidates) {
////                        if (currNodeCand != null) {
////                            synthCandidates.add(currNodeCand + "." + suffix);
////                        } else {
////                            synthCandidates.add(suffix);
////
////                        }
////                    }
////                }
////            } else {
////                synthCandidates.addAll(currNodeCandidates);
////            }
////        }
////        return synthCandidates;
////    }
//
//    private static void addCandidate(HashMap<Integer, Candidate> candidates, Candidate candidate) {
//        if (candidate != null) {
//            int candidateHash = candidate.getCandidate().hashCode();
//            if (!candidates.containsKey(candidateHash)) {
//                candidates.put(candidateHash, candidate);
//            }
//        }
//    }
//
//    private static String replaceCandidate(String source, String newString, int startPost, int endPos) {
//        logger.info(source + OSHelper.delimiter() + newString + OSHelper.delimiter() + startPost + OSHelper.delimiter() + endPos);
//        String format = source.substring(0, startPost) + newString + source.substring(endPos);
//        System.out.println(format);
//        return format;
//    }
//
//    //
////    private static HashMap<Integer, Candidate> findMethodCalledCandidate(Token targetNode, MethodNode methodNode, ClassNode classNode, FolderNode folderNode) {
////        HashMap<Integer, Candidate> candidates = new HashMap<>();
////        for (MethodNode method : classNode.getMethodList()) {
////            for (StatementNode stm : method.getStatementNodes()) {
////                Token token = findMethodCalledsToReplace(stm, targetNode);
////                for (MethodCalledNode methodCalledNode : methodCalledNodes) {
////                    generateCandidatesWithParams(methodCalledNode, candidates, targetNode, methodNode, classNode, folderNode);
////                }
////            }
////        }
////        return candidates;
////    }
//
//    private static List<Candidate> getCandidateList(HashMap<Integer, Candidate> candidateHashMap) {
//        if (candidateHashMap.size() > 0) {
//            List<Candidate> candidateList = new ArrayList<>();
//            for (Candidate candidate : candidateHashMap.values()) {
//                candidateList.add(candidate);
//            }
//            return candidateList;
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    private static HashMap<Integer, Candidate> generateCandidatesWithParams(MethodCalledNode methodCalledNode,
//                                                                            StatementNode targetNode, MethodNode methodNode,
//                                                                            FolderNode folderNode) {
//        HashMap<Integer, Candidate> candidates = new HashMap<>();
//        boolean isCandidate = true;
//        List<List<Candidate>> params = new ArrayList<>();
//        //PARAMS
//        if (methodCalledNode.getAgurements().size() == 0) {
//            Candidate candidate = formatMethodCandidate(methodCalledNode,
//                    null, ((StatementNode) targetNode));
////            float distance = CaculatorDistance.caculatorDistence(format, ((StatementNode) targetNode).getStatementString());
////            Candidate candidate = new Candidate(format, ((StatementNode) targetNode).getStartPostion(),
////                    ((StatementNode) targetNode).getEndPostion(), distance);
//            addCandidate(candidates, candidate);
//        } else {
//            for (StatementNode param : methodCalledNode.getAgurements()) {
//                if (!(param instanceof Token)) {
//                    List<Candidate> cds = new ArrayList<>();
//                    Candidate candidate = new Candidate(param.getStatementString(),
//                            ((StatementNode) targetNode).getStartPostion(),
//                            ((StatementNode) targetNode).getEndPostion(),
//                            1);
//                    cds.add(candidate);
//                    params.add(cds);
//                } else {
////                    List<Candidate> candidateParam = findParamToken(((Token) param).getType(), param, methodNode, classNode, param.getLine());
////                    if (param instanceof MethodInvocationStmNode) {
////                        param.setDeepLevel(targetNode.getDeepLevel() + 1);
////                    }
//                    List<Candidate> candidateParams = generateCandidate(param, methodNode, folderNode);
//                    if (candidateParams.size() < 1) {
//                        isCandidate = false;
//                        break;
//                    }
//                    params.add(candidateParams);
//                }
//            }
//            if (isCandidate) {
//                //Create candidate
//                List<List<Candidate>> paramSynthesis = synthesis(params, targetNode);
//                HashMap<Integer, Candidate> candidateHashMap = generateMethodCandidate(methodCalledNode, paramSynthesis, targetNode);
//                candidates.putAll(candidateHashMap);
//            }
//        }
//
//        return candidates;
//    }
//
//    private static HashMap<Integer, Candidate> generateMethodCalled(StatementNode targetNode, ClassNode classNode) {
//        HashMap<Integer, Candidate> candidates = new HashMap<>();
//        List<MethodNode> methodNodes = new ArrayList<>();
//        for (MethodNode methodNode : classNode.getMethodList()) {
//            if (methodNode.getReturnType().equals(targetNode.getType())) {
//                methodNodes.add(methodNode);
//            }
//        }
//        boolean isCandidate = true;
//        for (MethodNode methodNode : methodNodes) {
//            List<List<Candidate>> params = new ArrayList<>();
//            if (methodNode.getParameters().size() == 0) {
////                Candidate candidate = formatMethodCandidate(isSameMethod, methodNode, null, (StatementNode) targetNode);
////                Candidate candidateMethod = new Candidate(candidate, ((StatementNode) targetNode).getStartPostion(),
////                        ((StatementNode) targetNode).getEndPostion());
////                addCandidate(candidates, candidate);
//            } else {
//                //TODO: gen method
////                for (ParameterNode parameter : methodNode.getParameters()) {
////                    List<Candidate> candidateParam = generateCandidate()
////                    if (candidateParam.size() <= 0) {
////                        isCandidate = false;
////                        break;
////                    } else {
////                        params.add(candidateParam);
////                    }
////                }
////                if (isCandidate) {
////                    List<List<Candidate>> paramSynthesis = paramSynthesis(params);
////                    generateMethodCandidate(candidates, methodNode.getName(), paramSynthesis, targetNode);
////                }
//            }
//        }
//        return candidates;
//    }
//
//    private static HashMap<Integer, Candidate> generateMethodCandidate(
//            MethodCalledNode methodCalledNode, List<List<Candidate>> paramSynthesis,
//            StatementNode targetNode) {
//        HashMap<Integer, Candidate> candidates = new HashMap<>();
//
//        for (List<Candidate> comboParam : paramSynthesis) {
//            Candidate candidate = formatMethodCandidate(methodCalledNode, comboParam, (StatementNode) targetNode);
////            Candidate candidate = new Candidate(format, ((StatementNode) targetNode).getStartPostion(),
////                    ((StatementNode) targetNode).getEndPostion());
//            addCandidate(candidates, candidate);
//        }
//        return candidates;
//    }
//
//    private static List<List<Candidate>> synthesis(List<List<Candidate>> paramCandidates, StatementNode targetNode) {
//        List<List<Candidate>> paramSynthesis = new ArrayList<>();
//        List<Candidate> initSyn = new ArrayList<>();
//        paramSynthesis.add(initSyn);
//        List<List<Candidate>> tmpSynthesis = new ArrayList<>();
//
////        if (targetNode == bugNode) {
////            for (List<Candidate> candidateList : paramCandidates) {
////                subCandidateList(candidateList);
////            }
////        }
//        for (List<Candidate> param : paramCandidates) {
//            for (List<Candidate> synthesis : paramSynthesis) {
//                for (Candidate value : param) {
//                    List<Candidate> tmpValuesList = new ArrayList<>(synthesis);
//                    tmpValuesList.add(value);
//                    tmpSynthesis.add(tmpValuesList);
//                }
//            }
//            // swap
//            paramSynthesis.clear();
//            paramSynthesis.addAll(tmpSynthesis);
//            tmpSynthesis.clear(); // reinitialize
//        }
//        return paramSynthesis;
//    }
//
////    private static List<Candidate> findParamToken(String type, StatementNode oldNode, MethodNode methodNode, ClassNode classNode, int line) {
////        List<Candidate> params = new ArrayList<>();
////        //find field candidate in Method & class -- BaseVar
////        List<Candidate> paramCandidate = findInInitToLine(type, oldNode, methodNode, classNode, line);
////        params.addAll(paramCandidate);
////        //Find token candidate in method
////        findTokenCandidateToLine(type, oldNode, methodNode.getStatementNodes(), params, true, line);
////        //find Token in other message
////        return params;
////    }
//
//    private static void subCandidateList(List<Candidate> candidates) {
//        if (candidates.size() > 30) {
//            candidates.sort(Comparator.comparing(Candidate::getDistance));
//            Collections.reverse(candidates);
//            candidates.sort(Comparator.comparing(Candidate::getScore));
//            Collections.reverse(candidates);
//            candidates.subList(MAX_TOKEN, candidates.size()).clear();
//        }
//    }
//
//    private static HashMap<Integer, Candidate> findBaseVarNode(String type, StatementNode oldNode, MethodNode methodNode, int line) {
//        HashMap<Integer, Candidate> candidateHashMap = new HashMap<>();
//        for (InitNode initNode : methodNode.getInitNodes()) {
//            if (initNode.getLine() <= line) {
//                if (initNode.getType().equals(type)) {
//                    if (!candidateHashMap.containsKey(initNode.getVarname().hashCode())) {
//                        Candidate candidate = null;
//                        // replace candidate
//                        if (oldNode != null) {
////                            if (oldNode instanceof BaseVariableNode) {
////                                candidate = new Candidate(true, oldNode, initNode);
////                            } else {
//                            candidate = new Candidate(true, oldNode, initNode);
//                            candidateHashMap.put(initNode.getVarname().hashCode(), candidate);
//
//                        }
////                        else { // find candidate
////                            candidate = new Candidate(initNode.getVarname(), oldNode.getStartPostion(), oldNode.getEndPostion(),
////                                    0);
//////                        }
////                        if (candidate != null) {
////                        }
//                    }
//                }
//            }
//        }
//        for (InitNode initNode : ((ClassNode) methodNode.getParent()).getInitNodes()) {
//            if (initNode.getLine() <= oldNode.getLine()) {
//                if (initNode.getType().equals((oldNode).getType())) {
//                    if (!candidateHashMap.containsKey(initNode.getVarname().hashCode())) {
//                        Candidate candidate;
////                        if (oldNode instanceof BaseVariableNode) {
////                            candidate = new Candidate(false, oldNode, initNode);
////                        } else {
//                        candidate = new Candidate(false, oldNode, initNode);
////                        }
//                        candidateHashMap.put(initNode.getVarname().hashCode(), candidate);
//                    }
//                }
//            }
//        }
//        return candidateHashMap;
//    }
//
//    private static List<Candidate> genInInitToLine(String type, StatementNode oldNode, MethodNode methodNode, ClassNode classNode, int line) {
//        List<Candidate> params = new ArrayList<>();
//        for (InitNode initNode : methodNode.getInitNodes()) {
//            if (initNode.getLine() <= line) {
//                if (initNode.getType().equals(type)) {
//                    if (!params.contains(type)) {
//                        Candidate candidate;
//                        candidate = new Candidate(true, oldNode, initNode);
//                        params.add(candidate);
//                    }
//                }
//            }
//        }
//        for (InitNode initNode : classNode.getInitNodes()) {
//            if (initNode.getLine() <= oldNode.getLine()) {
//                if (initNode.getType().equals((oldNode).getType())) {
//                    if (!params.contains("this." + initNode.getVarname())) {
//                        Candidate candidate;
////                        if (oldNode instanceof BaseVariableNode) {
////                            candidate = new Candidate(Fix.FixType.ELEMENT, false, oldNode, initNode);
////                        } else {
//                        candidate = new Candidate(false, oldNode, initNode);
////                        }
//                        params.add(candidate);
//                    }
//                }
//            }
//        }
//
//        return params;
//    }
//
//    /**
//     * find other node in method
//     *
//     * @param oldNode
//     * @param statementNodes
//     * @param tokens
//     * @param isSameMethod
//     * @return
//     */
//    private static List<Candidate> findTokenCandidateToLine(String type, StatementNode oldNode,
//                                                            List<StatementNode> statementNodes,
//                                                            List<Candidate> tokens, boolean isSameMethod, int line) {
//        for (StatementNode stm : statementNodes) {
//            if (stm != null) {
//                if (stm.getLine() <= line) {
//                    if (stm instanceof Token) {
//                        if (stm instanceof MethodCalledNode) {
//                            findTokenCandidateToLine(type, oldNode, ((MethodCalledNode) stm).getAgurements(), tokens, isSameMethod, line);
//                        } else {
//                            if ((stm).getType() == null && (type == null)) {
//                                if (!(((Token) stm) instanceof InfixExpressionStmNode)) {
//                                    boolean isAdd = true;
//                                    if (stm instanceof BaseVariableNode) {
//                                        if (((BaseVariableNode) stm).getKeyVar() == null) {
//                                            isAdd = false;
//                                        }
//                                    }
//                                    if (isAdd) {
//                                        boolean exist = false;
//                                        for (Candidate candidate : tokens) {
//                                            if (candidate.getCandidate().equals(stm.getStatementString())) {
//                                                exist = true;
//                                                break;
//                                            }
//                                        }
//                                        if (!exist) {
//                                            Candidate candidate;
//                                            if (oldNode != null) {
//                                                candidate = new Candidate(oldNode, stm);
//                                            } else {
//                                                candidate = new Candidate(stm.getStatementString(),
//                                                        -1, -1, 0);
//                                            }
//                                            tokens.add(candidate);
//                                        }
//                                    }
//                                }
//                            } else {
//                                if ((stm).getType() != null && (type != null)) {
//                                    if ((stm).getType().equals(type)) {
//                                        boolean exist = false;
//                                        for (Candidate candidate : tokens) {
//                                            if (candidate.getCandidate().equals(stm.getStatementString())) {
//                                                exist = true;
//                                                break;
//                                            }
//                                        }
//                                        if (!exist) {
//                                            Candidate candidate;
//                                            candidate = new Candidate(oldNode, stm);
//                                            tokens.add(candidate);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if (stm != null) {
//                        if (stm.getChildren().size() > 0) {
//                            findTokenCandidateToLine(type, oldNode, stm.getChildren(), tokens, isSameMethod, line);
//                        }
//                    }
//                }
//            }
//        }
//        return tokens;
//    }
//
//    private static List<MethodCalledNode> findMethodCallInStatement(StatementNode statementNode) {
//        List<MethodCalledNode> methodCalledNodes = new ArrayList<>();
//        if (statementNode instanceof MethodCalledNode) {
//            methodCalledNodes.add((MethodCalledNode) statementNode);
//        }
//        if (statementNode != null) {
//            for (StatementNode child : statementNode.getChildren()) {
//                List<MethodCalledNode> methods = findMethodCallInStatement(child);
//                methodCalledNodes.addAll(methods);
//            }
//        }
//        return methodCalledNodes;
//    }
//
//    /**
//     * Get token with sourceToken.type == targetToken.type
//     *
//     * @param srcNode
//     * @param targetToken
//     * @return
//     */
//    private static List<StatementNode> findSourceCandidate(
//            List<Integer> hashes,
//            StatementNode srcNode,
//            StatementNode targetToken, boolean isSameMethod) {
//        List<StatementNode> tokens = new ArrayList<>();
//        boolean isAdd = false;
//        if (srcNode instanceof Token) {
//            if (!(srcNode instanceof BaseVariableNode)
//                    && !(srcNode instanceof InfixExpressionStmNode)) {
//                // ==== find same NULL ===
//                if ((srcNode).getType() == null
//                        && targetToken.getType() == null) {
//                    if (targetToken instanceof MethodCalledNode
//                            && srcNode instanceof MethodCalledNode) {
//                        if (((Token) targetToken).getHashCode() != ((Token) targetToken).getHashCode()) {
//                            //true when same type & same parent
//                            isAdd = compareMethodCalledNode(srcNode, targetToken);
//                        }
//                    }
//                    //is methodInvocation
//                    if (!(targetToken instanceof MethodCalledNode)
//                            && !(srcNode instanceof MethodCalledNode)) {
//                        if (srcNode instanceof MethodInvocationStmNode) {
//                            //not addMethodInvo of BugNode
//                            if (!elementsOfBugNode.contains(srcNode)) {
//                                if (targetToken.getDeepLevel() <= DEEP_LEVEL) {
//                                    targetToken.setDeepLevel(targetToken.getDeepLevel() + 1);
//                                    isAdd = true;
//                                }
//                            }
//                        } else {
//                            isAdd = true;
//                        }
//                    }
//                    //========== SameType ========
//                } else if ((srcNode).getType() != null && targetToken.getType() != null) {
//                    //same Type
//                    if ((srcNode).getType().equals(targetToken.getType())) {
//                        //method invo
//                        if (targetToken instanceof MethodCalledNode
//                                && srcNode instanceof MethodCalledNode) {
////                            if ((targetToken).getDeepLevel() <= DEEP_LEVEL) {
//                            if (((Token) targetToken).getHashCode() != ((Token) srcNode).getHashCode()) {
//                                //is same Parent? sameType?
//                                isAdd = compareMethodCalledNode(srcNode, targetToken);
//                            }
////                            }
//                        }
//                        if (!(targetToken instanceof MethodCalledNode)
//                                && !(srcNode instanceof MethodCalledNode)) {
//                            if (srcNode instanceof MethodInvocationStmNode) {
//                                //not addMethodInvo of BugNode
//                                if (!elementsOfBugNode.contains(srcNode)) {
//                                    if (targetToken.getDeepLevel() < DEEP_LEVEL) {
//                                        targetToken.setDeepLevel(targetToken.getDeepLevel() + 1);
//                                        isAdd = true;
//                                    }
//                                }
//                            } else {
//                                isAdd = true;
//                            }
//                        }
//                    }
//                }
//                if (isAdd) {
//                    srcNode.isSameMethod = isSameMethod;
//                    addToken(hashes, tokens, (srcNode));
//                }
//            }
//        }
//
//        if (srcNode instanceof MethodCalledNode) {
//            addParams(((MethodCalledNode) srcNode).getAgurements(),
//                    tokens, hashes, targetToken, isSameMethod);
//        }
////        else if (statementNode instanceof ConstructorInvocationNode) {
////            addParams(((ConstructorInvocationNode) statementNode).getAguments(),
////                    tokens, hashes, targetToken);
////        }
//        else if (srcNode instanceof ClassInstanceCreationNode) {
//            addParams(((ClassInstanceCreationNode) srcNode).getArgs(),
//                    tokens, hashes, targetToken, isSameMethod);
//        }
//        if (srcNode != null) {
//            addParams(srcNode.getChildren(),
//                    tokens, hashes, targetToken, isSameMethod);
//        }
//        return tokens;
//    }
//
//    private static void addParams(List<StatementNode> statementNodes,
//                                  List<StatementNode> tokens,
//                                  List<Integer> hashes, StatementNode targetToken, boolean isSameMethod) {
//        if (statementNodes.size() > 0) {
//            for (StatementNode child : statementNodes) {
//                List<StatementNode> tokenList = findSourceCandidate(hashes, child, targetToken, isSameMethod);
//                tokens.addAll(tokenList);
//            }
//        }
//    }
//
//    /**
//     * Get token with sourceToken.type == targetToken.type == null && instance of MethodCalled
//     * && SameParent
//     *
//     * @param statementNode
//     * @param targetToken
//     * @return
//     */
//    private static boolean compareMethodCalledNode(StatementNode statementNode, StatementNode targetToken) {
//        if (statementNode instanceof MethodCalledNode) {
//            // type == null
//            if ((statementNode).getType() == null && targetToken.getType() == null) {
//                StatementNode parentCandidate = statementNode.getParent();
//                StatementNode parenttarget = targetToken.getParent();
//                if (parenttarget != null && parentCandidate != null) {
//                    boolean isEqual = false;
//                    //compare parent
//                    if (parenttarget.getType() == null && parentCandidate.getType() == null) {
//                        int typeA = ((Token) parenttarget).getHashCode();
//                        int typeB = ((Token) parentCandidate).getHashCode();
//                        isEqual = typeA == typeB;
//                    } else if (parenttarget.getType() != null && parentCandidate.getType() != null) {
//                        isEqual = parentCandidate.getType().equals(parenttarget.getType());
//                    }
//                    //same parent & same type
//                    if (isEqual) {
//                        return true;
//                    }
//                }
//            }
//        }
////        if (statementNode != null) {
//////            if (statementNode.getChildren().size() > 0) {
////            for (StatementNode child : statementNode.getChildren()) {
////                List<MethodCalledNode> tokenList = findMethodCalledsToReplace(child, targetToken);
////                tokens.addAll(tokenList);
////            }
//////            }
////        }
//        return false;
//    }
//
//    private static void addToken(List<Integer> hashes, List<StatementNode> tokens, StatementNode statementNode) {
//        if (!hashes.contains(((Token) statementNode).getHashCode())) {
//            tokens.add(statementNode);
//            hashes.add(((Token) statementNode).getHashCode());
//        }
//    }
//
//
//    private static Candidate formatMethodCandidate(
//            MethodCalledNode methodCalledNode,
//            List<Candidate> keyParams, StatementNode
//                    targetNode) {
//        String format = "";
////        boolean isBugToken = false;
////        if (bugNode == targetNode) {
////            if (targetNode instanceof MethodCalledNode
////                    || targetNode instanceof MethodInvocationStmNode) {
////                isBugToken = true;
////            }
////        }
////        float score = 0;
//        if (keyParams == null) {
//            format = String.format("%s()", methodCalledNode.getMethodName());
//        } else if (keyParams.size() == 0) {
//            format = String.format("%s()", methodCalledNode.getMethodName());
//        } else {
//            String params = "";
//            if (keyParams.size() > 1) {
//                // > 1
//                for (int i = 0; i < keyParams.size() - 1; i++) {
//                    params += keyParams.get(i).getCandidate() + ",";
////                    if (isBugToken) {
////                        score = score == 0 ? 1 : score;
////                        score = score * keyParams.get(i).getScore();
////                    }
//                }
//                params += keyParams.get(keyParams.size() - 1).getCandidate();
////                if (isBugToken) {
////                    score = score == 0 ? 1 : score;
////                    score = score * keyParams.get(keyParams.size() - 1).getScore();
////                }
//            } else {
//                //==1
//                params = keyParams.get(0).getCandidate();
////                if (isBugToken) {
////                    score = score == 0 ? 1 : score;
////                    score = score * keyParams.get(0).getScore();
////                }
//            }
//            format = String.format("%s(%s)", methodCalledNode.getMethodName(), params);
//        }
////        float distance = CaculatorDistance.caculatorDistence(methodName, targetNode.getStatementString());
//        Candidate candidate = new Candidate(format, targetNode, methodCalledNode);
////        if (isBugToken) {
////            candidate.setScore(score);
////        }
//        return candidate;
//    }
//
//    //    private static List<Candidate> findCandidate
//
//    private static HashMap<Integer, Candidate> findInInitNodes(List<InitNode> initNodes, StatementNode
//            targetNode, boolean isField) {
//        HashMap<Integer, Candidate> candidates = new HashMap<>();
//        if (targetNode instanceof BaseVariableNode) {
//            for (InitNode initNode : initNodes) {
//                if (initNode.getLine() <= targetNode.getLine()) {
//                    if (initNode.getType().equals(((BaseVariableNode) targetNode).getType())) {
//                        String value = initNode.getVarname();
//                        float distance = CaculatorDistance.caculateDistance(initNode.getVarname()
//                                , ((BaseVariableNode) targetNode).getKeyVar());
//                        if (isField) {
//                            value = "this." + value;
//
//                        }
//
//                        Candidate candidate;
////                        if (targetNode instanceof BaseVariableNode) {
////                            candidate = new Candidate(Fix.FixType.ELEMENT, !isField, targetNode, initNode);
////                        } else {
//                        candidate = new Candidate(!isField, targetNode, initNode);
////                        }
//                        addCandidate(candidates, candidate);
//                    }
//                }
//            }
//        }
//        return candidates;
//    }
//
////    /**
////     * create element Candidate
////     *
////     * @param statementNode
////     * @return
////     */
////    private static List<Integer> parseBugNode(StatementNode statementNode,
////                                              int level) {
////        List<Integer> elements = new ArrayList<>();
////        if (statementNode != null) {
////            if (statementNode instanceof Token) {
////                boolean isAdd = true;
////                // excepts
////                if (statementNode instanceof BaseVariableNode) {
////                    if (((BaseVariableNode) statementNode).getKeyVar() == null) {
////                        isAdd = false;
////                    }
////                } else if (statementNode instanceof MethodInvocationStmNode) {
////                    isAdd = false;
////                }
//////                else if (statementNode instanceof MethodCalledNode) {
//////                    DEEP_AUTO++;
//////                    for (StatementNode stm : ((MethodCalledNode) statementNode).getAgurements()) {
//////                        List<Integer> hass = new ArrayList<>();
//////                        List<TokenCandidate> tokenCandidateList = createCandidates(hass, stm, level);
//////                        for (TokenCandidate token : tokenCandidateList) {
//////////                            if (!hashes.contains(((Token)token.getTargetNode()).getHashCode())) {
//////////                                TokenCandidate tokenCandidate = new TokenCandidate(statementNode);
//////                            tokenCandidates.add(token);
//////////                                hashes.add(((Token) statementNode).getHashCode());
//////////                            }
//////                        }
//////                    }
//////                }
////                if (isAdd) {
////                    elements.add(statementNode.hashCode());
////                }
////            }
////            //get child (except QualifierName)
////            if (statementNode.getChildren().size() > 0 && !(statementNode instanceof QualifiedNameNode)) {
////                level = level + 1;
////                for (StatementNode child : statementNode.getChildren()) {
////                    List<Integer> list = parseBugNode(child, level);
////                    elements.addAll(list);
////                }
////            }
////        }
////        return elements;
////    }
//
////}
////}
//
//    /**
//     * create element Candidate
//     *
//     * @param statementNode
//     * @return
//     */
//    private static List<TokenCandidate> parseTokenCandidates(List<StatementNode> hashes, StatementNode statementNode,
//                                                             int level) {
//        List<TokenCandidate> tokenCandidates = new ArrayList<>();
//        if (statementNode != null) {
//            if (statementNode instanceof Token) {
//                boolean isAdd = true;
//                // excepts
//                if (statementNode instanceof BaseVariableNode) {
//                    if (((BaseVariableNode) statementNode).getKeyVar() == null) {
//                        isAdd = false;
//                    }
//                }
//                if (isAdd) {
////                    if (!hashes.contains(((Token) statementNode).getHashCode())) {
//                    TokenCandidate tokenCandidate = new TokenCandidate(statementNode, level);
//                    tokenCandidates.add(tokenCandidate);
////                    hashes.add(((Token) statementNode).getHashCode());
////                    }
//                }
//            }
//            //get child (except QualifierName)
//            if (statementNode.getChildren().size() > 0 && !(statementNode instanceof QualifiedNameNode)) {
////                    level = level + 1;
//                //invocation is root
//                if (!(statementNode instanceof MethodInvocationStmNode)
//                        && !(statementNode instanceof InfixExpressionStmNode)) {
//                    for (StatementNode child : statementNode.getChildren()) {
//                        List<TokenCandidate> tokenCandidateList = parseTokenCandidates(hashes, child, level);
//                        tokenCandidates.addAll(tokenCandidateList);
//                    }
//                } else {
//                    hashes.add(statementNode); //to remove methodCall is param in Method
//                }
//            }
//        }
//
////        private static List<TokenCandidate> parserTokenCandidates(List<Integer> hashes, StatementNode statementNode,
////        int level) {
////            if (statementNode instanceof InfixExpressionStmNode) {
////                List<Token> tokens =
////            }
////        }
//
//        return tokenCandidates;
//    }
//
//    /**
//     * //TODO: create
//     */
//
//    public static void main(String[] args) {
//        // 3 params
//        List<String> paramA = new ArrayList<>();
//        paramA.add("a1");
//        paramA.add("a2");
//        paramA.add("a3");
//        List<String> paramB = new ArrayList<>();
//        paramB.add("b1");
//        paramB.add("b2");
//        paramB.add("b3");
//        paramB.add("b4");
//        List<String> paramC = new ArrayList<>();
//        paramC.add("c1");
//        paramC.add("c2");
//
//        List<List<String>> params = new ArrayList<>();
//        params.add(paramA);
//        params.add(paramB);
//        params.add(paramC);
//
////        paramSynthesis(params);
//    }
//
//}
