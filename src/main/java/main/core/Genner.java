package main.core;

import AST.node.ClassNode;
import AST.node.FolderNode;
import AST.node.MethodNode;
import AST.stm.abst.StatementNode;
import AST.stm.node.ArrayAccessNode;
import AST.stm.node.OperatorNode;
import AST.stm.token.*;
import com.google.common.collect.Lists;
import main.Board;
import main.FindingAPI;
import main.Runner;
import main.core.pattern.BasePattern;
import main.core.pattern.InfixPattern;
import main.core.pattern.MethodPattern;
import main.core.pattern.Pattern;
import main.core.token.MethodToken;
import main.core.token.OperatorToken;
import main.core.token.Token;
import main.core.token.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ReflectionHelper;

import java.util.*;
import java.util.concurrent.*;

public class Genner {
    private static Logger log = LoggerFactory.getLogger(Genner.class);

    private StatementNode targetToken;
    private MethodNode methodNode; // the method where the target token is
    private FolderNode folderNode;
    private String nodeType;
    //    private List<Pattern> patterns;
//    public final static int MAX_DEEP_LEVEL = 1;
    private HashMap<Integer, List<StatementNode>> nodeMap = new HashMap<>();

    public Genner() {
    }

    public Genner(StatementNode targetToken, FolderNode folderNode, MethodNode methodNode) {
        this.targetToken = targetToken;
        if (targetToken instanceof MethodInvocationStmNode) {
            this.nodeType = ((MethodInvocationStmNode) targetToken).getMethodType();
        } else {
            if (targetToken != null) {
                this.nodeType = targetToken.getType();
            }
        }
        this.folderNode = folderNode;
        this.methodNode = methodNode;
//        this.patterns = new ArrayList<>();
    }

    /**
     * @param targetNode a token candidate
     * @param methodNode the method where the target Node is
     * @param folderNode the folder contains
     * @return list of candidates for the target node
     */
    public SortedSet<String> generatePatches(StatementNode targetNode,
                                             MethodNode methodNode, FolderNode folderNode,
                                             Board.SuspCodeNode suspCodeNode) {
        SortedSet<String> patchCandidates = new TreeSet<>();
        try {
            Genner genner = new Genner(targetNode, folderNode, methodNode); // e.g. cfa.method(b,c)
            // 1. generate tokens for Node
            log.info("Generate tokens for Nodes");
            genner.genTokens(targetNode); // gen token for each node e.g. cfa -> <Type>, method(b, c) -> method(<int>, <int>)
            // 2. synthesize pattern for the target node
            log.info("Synthesize pattern for the target node");
            List<Pattern> patterns = genner.genPatterns(targetNode);
//        genner.setPatterns(Lists.reverse(patterns)); // <Type>.method(), cfa.anotherMethod(<int.)
            patterns = Lists.reverse(patterns);
            // 3. from synthesized patterns, generate source node candidate
            log.info("Generate candidates");
            patchCandidates = genner.genPatches(patterns, suspCodeNode);
            log.info("Finishing generate candidates");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return patchCandidates;
    }

    //(int) ((value - this.lowerBound.df) / (this.upperBound - this.lowerBound) * 255.0);

    private SortedSet<String> genPatches(List<Pattern> patterns, Board.SuspCodeNode sus) {
        SortedSet<String> patches = new TreeSet<>(Collections.reverseOrder());
        for (Pattern pattern : patterns) {
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<SortedSet<String>> task = new Callable<SortedSet<String>>() {
                public SortedSet<String> call() {
                    return generatePatchCandidate(pattern, sus);
                }
            };
            Future<SortedSet<String>> future = executor.submit(task);
            try {
                SortedSet<String> result = future.get(20, TimeUnit.SECONDS);
                patches.addAll(result);
            } catch (TimeoutException ex) {
                // handle the timeout
            } catch (InterruptedException e) {
                // handle the interrupts
            } catch (ExecutionException e) {
                // handle other exceptions
            } finally {
                future.cancel(true); // may or may not desire this
            }
        }
        return patches;
    }

    private List<Pattern> genPatterns(StatementNode targetNode) {
        List<Pattern> patterns = new ArrayList<>();
        boolean isSameMethodInvo = false; // Qualified same MethodInvocation: Class.Var
        boolean isSameBaseVar = false; //Qualified same base var: Enum.Var
        // Filter nodeType isSimple? or MethodInvocation
        if (targetNode instanceof QualifiedNameNode) {
            //TODO: edit with Class.Enum.var
            if (ReflectionHelper.isEnum(targetNode.getChildren().get(0).getType())) {
                isSameBaseVar = true;
            } else {
                isSameMethodInvo = true; // eg: that.field
            }
        } else if (targetNode instanceof BaseVariableNode) { //eg: this.field
            if (targetNode.getChildren().size() > 0) {
                if (targetNode.getChildren().get(0) instanceof BaseVariableNode) {
                    isSameMethodInvo = true;
                }
            }
        }
        // 1. if the target token is a base variable
        if (targetNode instanceof BaseVariableNode && !isSameMethodInvo
                || isSameBaseVar
                || targetNode instanceof BooleanNode) {
           patterns.addAll(genBaseVarPattern(targetNode));
        } else if (targetNode instanceof MethodInvocationStmNode) {
            return genMethodPatterns(targetNode, targetNode);
        } else if (targetNode instanceof ClassInstanceCreationNode) {
            return genMethodPatterns(targetNode, targetNode);
        } else if (targetNode instanceof InfixExpressionStmNode) {
            return genInfixPatterns(targetNode);
        } else if (isSameMethodInvo) {
            return genQualifierPattern(targetNode, targetNode);
        }
        return patterns;
    }

    private List<Pattern> genBaseVarPattern(StatementNode targetNode) {
        List<Pattern> patterns = new ArrayList<>();
        List<Token> tokens = targetNode.getToken() == null ? null : Arrays.asList(
                targetNode.getToken());
        if (tokens != null) {
            for (Token token : tokens) {
                Change change = new Change();
                change.setOriginalNode(targetNode);
                change.setTokenKey(token.hashCode());
                change.setToken(token);
                token.setOriginalValue(targetNode.toString()); // a basevar
                BasePattern basePattern = new BasePattern();
                basePattern.setTargetNode(targetNode);
                basePattern.getChanges().add(change);
                patterns.add(basePattern);
            }
        }
        return patterns;
    }

    private List<Pattern> genQualifierPattern(StatementNode targetNode, StatementNode currentNode) {
        List<Pattern> patterns = new ArrayList<>();
        // 1. Generate pattern for current node
        List<Token> tokens = currentNode.getToken() == null ? null : Collections.singletonList(currentNode.getToken());
        if (tokens != null) {
            for (Token token : tokens) {
                Change change = new Change();
                change.setOriginalNode(currentNode);
                change.setTokenKey(token.hashCode());
                change.setToken(token);

                BasePattern basePattern = new BasePattern();
                basePattern.setTargetNode(targetNode);
                basePattern.getChanges().add(change);
                patterns.add(basePattern);
            }
        }

        // 2. Generate for the rest of the method invocation
        if (currentNode.getChildren().size() > 1) {
            for (int i = 1; i < currentNode.getChildren().size(); i++) {
                if (currentNode.getChildren().get(i).getToken().getNodeType() != null) {
                    Change change = new Change();
                    change.setOriginalNode(currentNode.getChildren().get(0));
                    change.setToken((main.core.token.Token) currentNode.getChildren().get(i).getToken());
                    change.setTokenKey(((main.core.token.Token) currentNode.getChildren().get(i).getToken()).hashCode());

                    BasePattern basePattern = new BasePattern();
                    basePattern.setTargetNode(targetNode);
                    basePattern.getChanges().add(change);

                    patterns.add(basePattern);
                }
            }
        }

        if (currentNode.getChildren().size() > 0) {
            for (StatementNode child : currentNode.getChildren()) {
                patterns.addAll(genQualifierPattern(targetNode, child));
            }
        } else {
//                //child:
//                if (currentNode.getType() != null) {
//                    Token token = new TypeToken(targetNode, Token.Scope.ALL_AFTER);
//                    token.setNodeType(currentNode.getType());
//                    Change change = new Change();
//                    change.setOriginalNode(targetNode);
//                    change.setToken(token);
//
//                    BasePattern basePattern = new BasePattern();
//                    basePattern.setTargetNode(targetNode);
//                    basePattern.getChanges().add(change);
//                    patterns.add(basePattern);
//                }
        }

        return patterns;
    }

    private List<Pattern> genInfixPatterns(StatementNode statementNode) {
        List<Pattern> patterns = new ArrayList<>();
        if (statementNode instanceof InfixExpressionStmNode) {
            if (statementNode.getType() != null) {
                BasePattern basePattern = new BasePattern();
                basePattern.setTargetNode(statementNode);
                Change change = new Change();
                change.setOriginalNode(statementNode);
                TypeToken typeToken = new TypeToken(statementNode, Token.Scope.ALL_AFTER);
                typeToken.setNodeType(statementNode.getType());
                change.setToken(typeToken);
                change.setTokenKey(typeToken.hashCode());
                basePattern.setChanges(Arrays.asList(change));
                patterns.add(basePattern);
            }
            //infix token
            main.core.token.Token token = statementNode.getToken();
            // 1. Pattern for Operator
            if (token != null) {
                //create changes
                Change change = new Change();
                change.setOriginalNode(((InfixExpressionStmNode) statementNode).getOperator());
                change.setToken(token);
                change.setTokenKey(token.hashCode());

                //Sub pattern:
                BasePattern operatorPattern = new BasePattern();
                operatorPattern.setTargetNode(((InfixExpressionStmNode) statementNode).getOperator());
                operatorPattern.getChanges().add(change);
                patterns.add(operatorPattern);
            }
            // 2. Generate left pattern
            StatementNode left = ((InfixExpressionStmNode) statementNode).getLeft();
            //if left != instance of infix PatchInfix = add
            if (!(left instanceof InfixExpressionStmNode)) {
                if (left != null) {
                    List<Pattern> leftPatterns = genPatterns(left);
                    patterns.addAll(leftPatterns);
                }
            } else {
                List<Pattern> infixPatterns = genInfixPatterns(left);
                patterns.addAll(infixPatterns);
            }
            // 3. Generate right pattern
            StatementNode right = ((InfixExpressionStmNode) statementNode).getRight();
            if (!(right instanceof InfixExpressionStmNode)) {
                if (right != null) {
                    List<Pattern> rightPatterns = genPatterns(right);
                    patterns.addAll(rightPatterns);
                }
            } else {
                List<Pattern> infixPatterns = genInfixPatterns(right);
                patterns.addAll(infixPatterns);
            }
        }
        return patterns;
    }

    private List<InfixPattern> convertToInfixPatterns(List<Pattern> leftPatterns, StatementNode right, StatementNode operatorNode, StatementNode target) {
        List<InfixPattern> infixPatterns = new ArrayList<>();
        for (Pattern pattern : leftPatterns) {
            InfixPattern infixPattern = new InfixPattern();
            infixPattern.setSubPattern(pattern);
            Change change = new Change();
            change.setOriginalNode(target);
            target.setToken(pattern.getChanges().get(0).getToken());
            change.setToken(pattern.getChanges().get(0).getToken());
            change.setTokenKey(pattern.getChanges().get(0).getToken().hashCode());
            infixPattern.setChanges(Arrays.asList(change));
            infixPattern.setTargetNode(target);
            infixPattern.toString();
            infixPatterns.add(infixPattern);
        }
        return infixPatterns;
    }

    private List<InfixPattern> convertToInfixPatterns(StatementNode left, List<Pattern> rightPatterns, StatementNode operatorNode, StatementNode targetToken) {
        List<InfixPattern> infixPatterns = new ArrayList<>();
        for (Pattern pattern : rightPatterns) {
            InfixPattern infixPattern = new InfixPattern();
            infixPattern.setSubPattern(pattern);
            Change change = new Change();
            change.setOriginalNode(targetToken);
            targetToken.setToken(pattern.getChanges().get(0).getToken());
            change.setToken(pattern.getChanges().get(0).getToken());
            change.setTokenKey(pattern.getChanges().get(0).getToken().hashCode());
            infixPattern.setChanges(Arrays.asList(change));

            infixPattern.setTargetNode(targetToken);
            infixPatterns.add(infixPattern);
        }
        return infixPatterns;
    }


    private List<Pattern> genMethodPatterns(StatementNode methodInvo, StatementNode targetNode) {
        List<Pattern> patterns = new ArrayList<>();
        // 1. Generate a pattern for current node - genSimplePattern
        List<Token> tokens = targetNode.getToken() == null ? null : Collections.singletonList(targetNode.getToken());
        if (tokens != null) {
            for (Token token : tokens) {
                Change change = new Change();
                change.setOriginalNode(targetNode);
                change.setToken(token);
                change.setTokenKey(token.hashCode());

                BasePattern basePattern = new BasePattern();
                basePattern.setTargetNode(methodInvo);
                basePattern.getChanges().add(change);

                patterns.add(basePattern);
            }
        }

        // 2. Generate patterns for children
        if (targetNode.getChildren().size() > 1 && !(targetNode instanceof ArrayAccessNode)) {
            for (int i = 1; i < targetNode.getChildren().size(); i++) {
                Change change = new Change();
                change.setOriginalNode(targetNode.getChildren().get(0));
                change.setToken((main.core.token.Token) targetNode.getChildren().get(i));
                change.setTokenKey(((main.core.token.Token) targetNode.getChildren().get(i)).hashCode());

                BasePattern basePattern = new BasePattern();
                basePattern.setTargetNode(methodInvo);
                basePattern.getChanges().add(change);

                patterns.add(basePattern);
            }
        }
        //3. Patterns for arguments
        if (targetNode instanceof MethodCalledNode
                || targetNode instanceof ClassInstanceCreationNode) { // allowed to change more than one element (e.g. two arguments of a method)
            List<List<Pattern>> argumentList = new ArrayList<>();
            List<StatementNode> args = new ArrayList<>();
            if (targetNode instanceof MethodCalledNode) {
                args = ((MethodCalledNode) targetNode).getArguments();
            } else {
                args = ((ClassInstanceCreationNode) targetNode).getArgs();
            }
            for (StatementNode argument : args) {
                List<Pattern> argPatterns = genPatterns(argument);
                argPatterns.add(null);
                argumentList.add(argPatterns);
            }
            //Synthesis
            List<List<Pattern>> synthesis = new ArrayList<>();
            for (List<Pattern> arg : argumentList) {
                if (synthesis.size() == 0) {
                    for (Pattern pattern : arg) {
                        List<Pattern> list = new ArrayList<>();
                        list.add(pattern);
                        synthesis.add(list);
                    }
                } else {
                    List<List<Pattern>> tmpSynthesis = new ArrayList<>();
                    for (Pattern pattern : arg) {
                        for (List<Pattern> synthesizedList : synthesis) {
                            List<Pattern> list = new ArrayList<>(synthesizedList);
                            list.add(pattern);
                            tmpSynthesis.add(list);
                        }
                    }

                    synthesis.clear();
                    synthesis.addAll(tmpSynthesis);
                }
            }

            for (List<Pattern> combination : synthesis) {
                boolean isContinue = true;
                if (combination.get(0) != null) {
                    if (combination.size() == 1) {
                        if (combination.get(0).getTargetNode().getParent()
                                instanceof InfixExpressionStmNode) {
                            patterns.add(combination.get(0));
                            isContinue = false;
                        }
                    }
                }
                if (isContinue) {
                    MethodPattern pattern = new MethodPattern();
                    pattern.setTargetNode(methodInvo);
                    pattern.setChangedMethod(targetNode);

                    Map<StatementNode, Pattern> changedArgsMap = new HashMap<>();
                    for (Pattern pt : combination) {
                        if (pt != null) {
                            pattern.getChanges().addAll(pt.getChanges());
                            changedArgsMap.put(pt.getTargetNode(), pt);
                        }
                    }
                    pattern.setChangedArgsMap(changedArgsMap);
                    if (pattern.getChanges().size() > 0)
                        patterns.add(pattern);
                }
            }
        }

        if (targetNode.getChildren().size() > 0)
            patterns.addAll(genMethodPatterns(methodInvo, targetNode.getChildren().get(0)));

        return patterns;

    }

    private void genTokens(StatementNode node) {
        boolean isSameMethodInvo = false; // Qualified same MethodInvocation: Class.Var
        boolean isSameBaseVar = false; //Qualified same base var: Enum.Var
        if (node instanceof QualifiedNameNode) {
            //TODO: edit with Class.Enum.var
            if (ReflectionHelper.isEnum(node.getChildren().get(0).getType())) {
                isSameBaseVar = true;
            } else {
                isSameMethodInvo = true;
            }
        }
        if (node instanceof MethodInvocationStmNode || isSameMethodInvo) {
            // we generate Token for the whole method invocation
            // method invocation -> <returnType>

            StatementNode child = node.getChildren().get(0);
            if (child.getChildren().size() > 0) { // when the child has child
                TypeToken typeToken = new TypeToken(child, Token.Scope.ALL_AFTER);
                typeToken.setNodeType(this.nodeType);
                typeToken.setOriginalValue(node.toString()); // redundant
//                node.getChildren().add(typeToken); // same level of the child node
                node.setToken(typeToken);
            }
        } else if (node instanceof BaseVariableNode || isSameBaseVar) {
            // e.g. var node in var.method(b, c).method(d, e)
            // => <varType>.method(b,c).method(d,e)
            // or var.<returnType>
            // 1. var -> <varType>
            // 2. method(b, c).method(d, e) -> <returnType>

            // 1. generate token for the current node
            TypeToken typeToken = null;
            if (!isSameBaseVar) {
                typeToken = new TypeToken(node, Token.Scope.ONLY_CURRENT); //baseVar
            } else {
                typeToken = new TypeToken(node, Token.Scope.ALL_AFTER); //qualifier
            }
            typeToken.setNodeType(node.getType());
//            typeToken.setNodeType(node.getType());
            typeToken.setOriginalValue(node.toString());
            // ===save to tokenMap (candidates)
//            List<Token> tokens = new ArrayList<>();
//            tokens.add(token);
            node.setToken(typeToken);

            // 2. generate candidate tokenes for the rest of the method invocation (e.g. method(b,c).method(d,e) -> <returnType>)
            // Run when the first child of this node is parent of another node (e.g. method(b,c) is parent of method(d,e))
            if (!isSameBaseVar) {
                StatementNode child = null;
                if (node.getChildren().size() > 0) child = node.getChildren().get(0);
                if (child != null && child.getChildren().size() > 0) { // the child has child
                    TypeToken rest = new TypeToken(child, Token.Scope.ALL_AFTER);
                    rest.setNodeType(this.nodeType);
                    String suffix = child.getSuffix();
                    suffix = suffix.equals("") ? suffix : "." + suffix;
                    rest.setOriginalValue(child.toString() + suffix);
                    node.getChildren().add(rest); // same level of the child node
                }
            }
        } else if (node instanceof MethodCalledNode) {
            // e.g. method1(b,c).method2(b2,c2).method3(b3, c3)
            // => (1) <method1ReturnType>.method2(b2,c2).method3(b3, c3)
            //    (2) method1(b,c).<returnType>
            //    (3) method1(<int>, c).method2(b2,c2).method3(b3, c3)
            //    (3) method1(b, <int>).method2(b2,c2).method3(b3, c3)
            //    (3) method1(<int>, <int>).method2(b2,c2).method3(b3, c3)

            // 1. generate token for the current node
            TypeToken typeToken = new TypeToken(node, Token.Scope.ONLY_CURRENT);
            typeToken.setNodeType(node.getType());
            typeToken.setOriginalValue(node.toString());
            // ===save to tokenMap (candidates)
//            List<Token> tokens = new ArrayList<>();
//            tokens.add(normToken);
            node.setToken(typeToken);

            // 2. generate method token for the rest of the method invocation
            StatementNode child = null;
            if (node.getChildren().size() > 0) child = node.getChildren().get(0);
            if (child != null && child.getChildren().size() > 0) { // the child has child
                Token restToken = new TypeToken(child, Token.Scope.ALL_AFTER);
                restToken.setNodeType(this.nodeType);
                String suffix = child.getSuffix();
                suffix = suffix.equals("") ? suffix : "." + suffix;
                restToken.setOriginalValue(child.toString() + suffix);
                node.getChildren().add(restToken); // same level of the child node
            }
            // 3. generate tokenes for all parameters of the method
            for (StatementNode param : ((MethodCalledNode) node).getArguments()) {
                //add level
                param.setStmBugDeepLevel(param, node.getStmBugDeepLevel() + 1);

                Genner paramGenner = new Genner(param, this.folderNode, this.methodNode);
                paramGenner.genTokens(param);
            }

        } else if (node instanceof ClassInstanceCreationNode) {
            // 1. generate token for the current node
            TypeToken typeToken = new TypeToken(node, Token.Scope.ONLY_CURRENT);
            typeToken.setNodeType(node.getType());
            typeToken.setOriginalValue(node.toString());
            // ===save to tokenMap (candidates)
//            List<Token> tokens = new ArrayList<>();
//            tokens.add(normToken);
            node.setToken(typeToken);

            // 2. generate patterns for children
            StatementNode child = null;
            if (node.getChildren().size() > 0) child = node.getChildren().get(0);
            if (child != null && child.getChildren().size() > 0) { // the child has child
                TypeToken rest = new TypeToken(child, Token.Scope.ALL_AFTER);
                rest.setNodeType(this.nodeType);
                String suffix = child.getSuffix();
                suffix = suffix.equals("") ? suffix : "." + suffix;
                rest.setOriginalValue(child.toString() + suffix);
                node.getChildren().add(rest); // same level of the child node
            }
            // 3. generate tokenes for all arguments of the method
            for (StatementNode param : ((ClassInstanceCreationNode) node).getArgs()) {
                param.setStmBugDeepLevel(param, node.getStmBugDeepLevel() + 1);
                Genner paramGenner = new Genner(param, this.folderNode, this.methodNode);
                paramGenner.genTokens(param);
            }
        } else if (node instanceof InfixExpressionStmNode) {
            //1. a/b * c-> <int>
            // <a> / b * c; <
            OperatorToken operator = generateOperatorToken((InfixExpressionStmNode) node);
            if (operator != null) {
                node.setToken(operator);
            }
            Genner leftNode = new Genner(((InfixExpressionStmNode) node).getLeft(), this.folderNode, this.methodNode);
            if (leftNode != null) {
                if (((InfixExpressionStmNode) node).getLeft() != null) {
                    leftNode.genTokens(((InfixExpressionStmNode) node).getLeft());
                }
            }
            Genner rightNode = new Genner(((InfixExpressionStmNode) node).getRight(), this.folderNode, this.methodNode);
            if (rightNode != null) {
                if (((InfixExpressionStmNode) node).getRight() != null) {
                    rightNode.genTokens(((InfixExpressionStmNode) node).getRight());
                }
            }
        } else if (node instanceof BooleanNode) {
            TypeToken token = new TypeToken(node, Token.Scope.ONLY_CURRENT);
            token.setNodeType(Genner.BooleanType.BooleanNode.toString());
            token.setOriginalValue(node.toString());
            node.setToken(token);
        }

        if (node.getChildren().size() > 0 && !isSameBaseVar && !(node instanceof InfixExpressionStmNode)) {
            for (StatementNode child : node.getChildren())
                genTokens(child);
        }

    }

    private OperatorToken generateOperatorToken(InfixExpressionStmNode statementNode) {
        OperatorNode operator = statementNode.getOperator();
        for (String operatorType : operatorCandidates.keySet()) {
            List<String> values = operatorCandidates.get(operatorType);
            if (values.contains(" "+((InfixExpressionStmNode) statementNode)
                    .getOperator().getOperator() + " ")) {
                OperatorToken operatorToken = new OperatorToken(statementNode.getOperator(),
                        Token.Scope.ONLY_CURRENT);
                operatorToken.setNodeType(operatorType.toString());
                operatorToken.setOriginalValue(operator.getOperator());
                return operatorToken;
            }
        }
        return null;
    }


    private SortedSet<String> generatePatchCandidate(Pattern pattern, Board.SuspCodeNode sus) {
//        if (pattern instanceof InfixPattern) {
//            ((InfixPattern) pattern).genCandidates(this);
//        } else {
        //save to candidates
//            pattern.setMethodNode(this.methodNode);
        SortedSet<String> patchCandidates = pattern.computePatchCandidates(this, sus);
        return patchCandidates;
//        }
    }

    // ================= GENERATE CANDIDATE ========================
    public void generateCandidate(Token token) {
        if (token instanceof TypeToken) {
            token.setCandidates(new ArrayList<>());
            if (token.getNodeType() != null) {
                //check enum &methodNode find candidates
                if (ReflectionHelper.isEnum(token.getNodeType())) {
                    List<StatementNode> qualifiedNameNodes = ReflectionHelper.findEnum(token.getNodeType());
                    if (token.getTarget() != null) {
                        for (StatementNode qualifiedName : qualifiedNameNodes) {
                            if (!qualifiedName.toString().contains(token.getTarget().toString())) {
                                token.getCandidates().add(qualifiedName);
                            }
                        }
                    } else {
                        token.getCandidates().addAll(qualifiedNameNodes);
                    }
                }
                List<BaseVariableNode> baseVarCandies = new ArrayList<>();
                //find basevar candidates
                if (token.getTargetScope() == Token.Scope.ONLY_CURRENT) {
                    baseVarCandies = findBaseVarSameType(token.getParentType(),
                            token.getNodeType(), this.targetToken.getLine(), this.methodNode);
                } else if (token.getTargetScope() == Token.Scope.ALL_AFTER) {
                    baseVarCandies = findBaseVarSameType(null,
                            token.getNodeType(), this.targetToken.getLine(), this.methodNode);
                }
                token.getCandidates().addAll(baseVarCandies);

                //gen for booleanNode true -> false, false -> true

                if (token.getNodeType().equals(Genner.BooleanType.BooleanNode.toString())) {
                    BooleanNode booleanNode = genBooleanCandidates((BooleanNode) token.getTarget());
                    token.getCandidates().add(booleanNode);
                }
            }
            //find method sameType and can access (should rank 2)
            List<StatementNode> methods = findMethodCalledSameType(token.getParentType(),
                    token.getNodeType(), (ClassNode) this.methodNode.getParent());
//            List<StatementNode> statementNodes = new ArrayList<>(methods);

            // if Constructor -> find constructor candidates
            if (token.getTarget() instanceof ClassInstanceCreationNode) {
                List<ClassInstanceCreationNode> constructors = ReflectionHelper
                        .findConstructorSameType(token.getNodeType(), ((ClassInstanceCreationNode) token.getTarget()).getArgs().size());
                methods.addAll(constructors);
            }

            if (token.getDeepLevel() < Runner.LEVEL) {
                ((TypeToken) token).setMethodTokens(new ArrayList<>());
                for (StatementNode method : methods) {
                    if (method instanceof MethodCalledNode) {
                        if (((MethodCalledNode) method).getArguments().size() == 0) {
                            token.getCandidates().add(getMethodCall(method));
                        } else {
                            boolean isGen = true;

                            if (token.getTarget() instanceof MethodCalledNode) {
                                //if same Params & != name
                                if (compareParams(method, token.getTarget())) {
                                    MethodCalledNode methodCalledNode = new MethodCalledNode(((MethodCalledNode) method).getMethodName());
                                    methodCalledNode.setAgurementTypes(((MethodCalledNode) token.getTarget()).getArguments());
                                    methodCalledNode.setStatementString(methodCalledNode.toString());
                                    token.getCandidates().add(methodCalledNode);
                                    if (token.getTarget().toString().replace(" ", "")
                                            .equals(methodCalledNode.toString().replace(" ", ""))) {
                                        methodCalledNode.findInClass = true;
                                    } else {
                                        methodCalledNode.findInClass = false;
                                    }
                                    isGen = false;
                                }
                            }
                            if (isGen) {
                                MethodToken methodToken = new MethodToken((MethodCalledNode) method, token.getDeepLevel());
                                ((TypeToken) token).getMethodTokens().add(methodToken);
                                methodToken.setTarget(getMethodCall(method));
                                generateCandidate(methodToken);
                                token.getCandidates().addAll(methodToken.getCandidates());

                            }
                        }
                    } else if (method instanceof ClassInstanceCreationNode) {
                        if (((ClassInstanceCreationNode) method).getArgs().size() == 0) {
                            token.getCandidates().add(method);
                        } else {

                            MethodToken methodToken = new MethodToken((ClassInstanceCreationNode) method, token.getDeepLevel());
                            ((TypeToken) token).getMethodTokens().add(methodToken);
                            generateCandidate(methodToken);

                            token.getCandidates().addAll(methodToken.getCandidates());
                        }
                    }
                }
            }
        } else if (token instanceof MethodToken) {
            //synthesis params
            for (Token argToken : ((MethodToken) token).getArgTokens()) {
                if (argToken.getCandidates().size() == 0) {
                    generateCandidate(argToken);
                }
            }

            token.setCandidates(new ArrayList<>());

            List<List<StatementNode>> synthesis = new ArrayList<>();
            for (Token argToken : ((MethodToken) token).getArgTokens()) {
                if (synthesis.size() == 0) {
                    for (StatementNode arg : argToken.getCandidates()) {
                        List<StatementNode> arguments = new ArrayList<>();
                        arguments.add(arg);
                        synthesis.add(arguments);
                    }
                    continue;
                }

                List<List<StatementNode>> tmpSynthesis = new ArrayList<>();
                for (List<StatementNode> synthesizedArgs : synthesis) {
                    for (StatementNode arg : argToken.getCandidates()) {
                        List<StatementNode> newArgs = new ArrayList<>(synthesizedArgs);
                        newArgs.add(arg);
                        tmpSynthesis.add(newArgs);
                    }
                }

                synthesis.clear();
                synthesis.addAll(tmpSynthesis);
            }

            for (List<StatementNode> args : synthesis) {
                if (!((MethodToken) token).isConstructor()) {
                    MethodCalledNode methodCalledNode = new MethodCalledNode(
                            ((MethodToken) token).getMethodName());
                    methodCalledNode.setAgurementTypes(args);
                    methodCalledNode.setStatementString(methodCalledNode.toString());
                    if (token.getTarget() instanceof MethodInvocationStmNode) {
                        MethodInvocationStmNode invo = new MethodInvocationStmNode(methodCalledNode);
                        if (token.getTarget().toString().replace(" ", "")
                                .equals(methodCalledNode.toString().replace(" ", ""))) {
                            invo.findInClass = true;
                        } else {
                            invo.findInClass = false;
                        }
                        token.getCandidates().add(invo);
                    } else {
                        if (token.getTarget().toString().replace(" ", "")
                                .equals(methodCalledNode.toString().replace(" ", ""))) {
                            methodCalledNode.findInClass = true;
                        } else {
                            methodCalledNode.findInClass = false;
                        }
                        token.getCandidates().add(methodCalledNode);
                    }
                    //TODO check exist
//                    List<StatementNode> statementNodes = nodeMap.get()
                } else {
                    ClassInstanceCreationNode classInstanceCreationNode =
                            new ClassInstanceCreationNode(((MethodToken) token).getMethodName(), args,
                                    null, -1, null);
                    classInstanceCreationNode.setStatementString(classInstanceCreationNode.toString());
                    classInstanceCreationNode.findInClass = false;
                    token.getCandidates().add(classInstanceCreationNode);
                }
            }
        } else if (token instanceof OperatorToken) {
            List<StatementNode> patches = genOperatorCandidates((OperatorToken) token);
            if (patches != null) {
                token.getCandidates().addAll(patches);
            }
        }
    }


//    public List<PatchCandidate> genOperatorCandidates(OperatorToken o, Pattern pattern, StatementNode target) {
//        List<PatchCandidate> patchCandidates = new ArrayList<>();
//        List<String> values = operatorCandidates.get(o.getNodeType());
//        for (String value : values) {
//            if (!value.equals(o.getOriginalValue())) {
//                PatchCandidate patchCandidate = new PatchCandidate(target);
//                patchCandidate.setPattern(pattern);
//                patchCandidate.getChangesMap().put(o, new OperatorNode(value, o.getStartPostion(), o.getEndPostion()));
//                patchCandidate.computeContent(pattern);
////                patchCandidate.setDistanceScore(0.5f);
//                patchCandidate.setPatternLevel(1);
////                patchCandidate.setScore(0.5f);
//                patchCandidates.add(patchCandidate);
//            }
//        }
//        return patchCandidates;
//    }

    public List<StatementNode> genOperatorCandidates(OperatorToken o) {
        List<PatchCandidate> patchCandidates = new ArrayList<>();
        List<String> values = operatorCandidates.get(o.getNodeType());
        List<StatementNode> operatorNodes = new ArrayList<>();
        for (String value : values) {
            if (!value.equals(" " + o.getOriginalValue() + " ")) {
                operatorNodes.add(new OperatorNode(value, o.getStartPostion(), o.getEndPostion()));
//                PatchCandidate patchCandidate = new PatchCandidate(target);
//                patchCandidate.setPattern(pattern);
//                patchCandidate.getChangesMap().put(o, new OperatorNode(value, o.getStartPostion(), o.getEndPostion()));
//                patchCandidate.computeContent(pattern);
//                patchCandidate.setDistanceScore(0.5f);
//                patchCandidate.setPatternLevel(1);
//                patchCandidate.setScore(0.5f);
//                patchCandidates.add(patchCandidate);
            }
        }
        return operatorNodes;
    }

    private BooleanNode genBooleanCandidates(BooleanNode booleanNode) {
        List<String> values = booleanCandies.get(Genner.BooleanType.BooleanNode.toString());
        for (String value : values) {
            if (!value.equals(booleanNode.toString())) {
                BooleanNode newCandi = new BooleanNode(value);
                return newCandi;
            }
        }
        return null;
    }

    private StatementNode getMethodCall(StatementNode statementNode) {
        if (statementNode instanceof MethodCalledNode) {
            if (statementNode.getParent() instanceof MethodInvocationStmNode) {
                if (((MethodInvocationStmNode) statementNode.getParent()).getNodes().size()
                        == 1) {
                    return statementNode.getParent();
                }
            }
        }
        return statementNode;
    }

    //===================== END Generate Candidate ==========================
    //===================== API ========================

    /**
     * Api find baseVarNodes
     *
     * @param methodNode
     * @return
     */
    private static List<BaseVariableNode> findBaseVarSameType(String parentClass, String type, int line, MethodNode methodNode) {
        return ReflectionHelper.findBaseVarSameType(parentClass,
                type, line, methodNode);
    }

    /**
     * API find MethodCalledNodes same type
     *
     * @param classNode
     * @return
     */
    private List<StatementNode> findMethodCalledSameType(String parentType, String itType, ClassNode classNode) {
        List<StatementNode> statementNodes = new ArrayList<>();
        Integer key;
        if (itType != null) {
            key = itType.hashCode();
        } else {
            key = "null".hashCode();
        }
        if (parentType != null) {
            key = (parentType + itType).hashCode();
        }
        if (!nodeMap.containsKey(key)) {
            HashMap<Integer, List<StatementNode>> listHashMap = parserClass(parentType, itType, classNode);
            nodeMap.putAll(listHashMap);
        }
        //get in list
        if (nodeMap.get(key) != null) {
            for (StatementNode stm : nodeMap.get(key)) {
                if (stm instanceof MethodCalledNode) {
                    stm.findInClass = true;
                    statementNodes.add((MethodCalledNode) stm);
                } else if (stm instanceof ClassInstanceCreationNode) {
                    stm.findInClass = true;
                    statementNodes.add(stm);
                }
            }
        }

        //get in class
        List<MethodCalledNode> statementNodes1 = ReflectionHelper.findMethodCalledSameType(parentType, itType, classNode);
        statementNodes.addAll(statementNodes1);
        return statementNodes;
    }

    private boolean compareParams(StatementNode stmNode, StatementNode target) {
        List<String> paramsA = getParamTypes((MethodCalledNode) stmNode);
        List<String> paramsB = getParamTypes((MethodCalledNode) target);
        return ReflectionHelper.compareParams(paramsA, paramsB);
    }

    private List<String> getParamTypes(MethodCalledNode methodCalledNode) {
        List<String> params = new ArrayList<>();
        for (StatementNode type : methodCalledNode.getArguments()) {
            params.add(type.getType());
        }
        return params;
    }

    private static HashMap<Integer, List<StatementNode>> parserClass(String parentType, String itType, ClassNode classNode) {
        HashMap<Integer, List<StatementNode>> map = new HashMap<>();
        for (MethodNode methodNode : classNode.getMethodList()) {
            for (StatementNode statementNode : methodNode.getStatementNodes()) {
                HashMap<Integer, List<StatementNode>> listHashMap = FindingAPI
                        .parseFile(statementNode, parentType, itType);
                for (Integer key : listHashMap.keySet()) {
                    if (map.containsKey(key)) {
                        FindingAPI.addStmNode(map.get(key), listHashMap.get(key));
                    } else {
                        map.put(key, listHashMap.get(key));
                    }
                }
            }
        }
        return map;
    }

    //=================== End API =========================

    public StatementNode getTargetToken() {
        return targetToken;
    }

    public void setTargetToken(StatementNode targetToken) {
        this.targetToken = targetToken;
    }

    public MethodNode getMethodNode() {
        return methodNode;
    }

    public void setMethodNode(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    public FolderNode getFolderNode() {
        return folderNode;
    }

    public void setFolderNode(FolderNode folderNode) {
        this.folderNode = folderNode;
    }

//    public List<Pattern> getPatterns() {
//        return patterns;
//    }
//
//    public void setPatterns(List<Pattern> patterns) {
//        this.patterns = patterns;
//    }

    private enum OperatorType {
        Relational_Operator,  // >, >=, <, <=
        Conditional_Operator, // &&, ||
        Equality_Operator, // ==, !=
        Pre_Operator // "!", ""
    }

    private enum BooleanType {
        BooleanNode
    }

    private static final Map<String, List<String>> operatorCandidates = new HashMap<String, List<String>>() {
        {
            List<String> infix1 = Arrays.asList(" > ", " >= ", " < ", " <= ");
            put(Genner.OperatorType.Relational_Operator.toString(), infix1);

            List<String> infix2 = Arrays.asList("!", "");
            put(Genner.OperatorType.Pre_Operator.toString(), infix2);

            List<String> infix3 = Arrays.asList(" == ", " != ");
            put(Genner.OperatorType.Equality_Operator.toString(), infix3);

            List<String> infix4 = Arrays.asList(" && ", " || ");
            put(Genner.OperatorType.Conditional_Operator.toString(), infix4);
        }
    };

    private static final Map<String, List<String>> booleanCandies = new HashMap<String, List<String>>() {
        {
            {
                List<String> infix4 = Arrays.asList("true", "false");
                put(Genner.BooleanType.BooleanNode.toString(), infix4);
            }
        }
    };

    private StatementNode saveMethodCalled(StatementNode stmNode, Token token, List<StatementNode> args) {
        MethodCalledNode methodCalledNode = new MethodCalledNode(((MethodCalledNode) stmNode).getMethodName());
        methodCalledNode.setAgurementTypes(args);
        methodCalledNode.setStatementString(methodCalledNode.toString());
        StatementNode result;
        if (getMethodCall(stmNode) instanceof MethodInvocationStmNode) {
            MethodInvocationStmNode methodInvocationStmNode = new MethodInvocationStmNode(methodCalledNode);
            methodInvocationStmNode.findInClass = true;
            result = methodInvocationStmNode;
        } else {
            result = methodCalledNode;
        }
        return result;
    }


}
