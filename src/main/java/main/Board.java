package main;

import AST.node.ClassNode;
import AST.node.FolderNode;
import AST.node.MethodNode;
import AST.stm.abst.StatementNode;
import config.Configuration;
import faultlocalization.dataprepare.DataPreparer;
import faultlocalization.gzoltar.FL;
import faultlocalization.objects.SuspiciousPosition;
import main.core.PatchCandidate;
import main.obj.CandidateElement;
import main.patch.TokenPatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class Board {

    private static Logger log = LoggerFactory.getLogger(Board.class);

    public String metric = "Ochiai";          // Fault localization metric.
    protected String path = "";
    protected String buggyProject = "";     // The buggy project name.
    protected String defects4jPath;         // The path of local installed defects4j.
    public int minErrorTest;                // Number of failed test cases before fixing.
    protected int minErrorTestAfterFix = 0; // Number of failed test cases after fixing
    protected String fullBuggyProjectPath;  // The full path of the local buggy project.
    public String outputPath = "";          // Output path for the generated patches.
    public File suspCodePosFile = null;     // The file containing suspicious code positions localized by FL tools.
    protected DataPreparer dp;              // The needed data of buggy program for compiling and testing.

    private String failedTestCaseClasses = ""; // Classes of the failed test cases before fixing.
    // All specific failed test cases after testing the buggy project with defects4j command in Java code before fixing.
    protected List<String> failedTestStrList = new ArrayList<>();
    // All specific failed test cases after testing the buggy project with defects4j command in terminal before fixing.
    protected List<String> failedTestCasesStrList = new ArrayList<>();
    // The failed test cases after running defects4j command in Java code but not in terminal.
    private List<String> fakeFailedTestCasesList = new ArrayList<>();

    // 0: failed to fix the bug, 1: succeeded to fix the bug. 2: partially succeeded to fix the bug.
    public int fixedStatus = 0;
    public String dataType = "";
    protected int patchId = 0;

    public void setRunTest(boolean runTest) {
        this.runTest = runTest;
    }

    public boolean runTest = false;

    public Board(String path, String projectName, String defects4jPath, boolean run) {
        this.path = path;
        this.buggyProject = projectName;
        fullBuggyProjectPath = path + buggyProject;
        this.defects4jPath = defects4jPath;
        this.runTest = run;
        if (run) {
            Path dir = Paths.get(fullBuggyProjectPath);
            if (Files.exists(Paths.get(fullBuggyProjectPath)) ){
                try {
                    Files.walk(dir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!Files.exists(Paths.get(fullBuggyProjectPath))) {
                TestUtils.cloneProjectWithDefects4j(path + buggyProject, defects4jPath);
            }
            if (FileHelper.getAllFiles(path + buggyProject + PathUtils.getSrcPath(buggyProject).get(0), ".class") == null) {
                TestUtils.compileProjectWithDefects4j(path + buggyProject, defects4jPath);
            }
            minErrorTest = TestUtils.getFailTestNumInProject(path + buggyProject, defects4jPath, failedTestStrList);
            if (minErrorTest == Integer.MAX_VALUE) {
                TestUtils.compileProjectWithDefects4j(path + buggyProject, defects4jPath);
                minErrorTest = TestUtils.getFailTestNumInProject(path + buggyProject, defects4jPath, failedTestStrList);
            }
        }
        log.info(buggyProject + " Failed Tests: " + this.minErrorTest);

        // Read paths of the buggy project.
        this.dp = new DataPreparer(path);
        dp.prepareData(buggyProject);
        readPreviouslyFailedTestCases();
    }

    private void readPreviouslyFailedTestCases() {
//        String[] failedTestCases = FileHelper.readFile(Configuration.failedTestCasesFilePath + this.buggyProject + ".txt").split("\n");
        List<String> failedTestCasesList = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        for (int index = 0; index < this.failedTestStrList.size(); index++) {
            // - org.jfree.data.general.junit.DatasetUtilitiesTests::testBug2849731_2
            String failedTestCase = failedTestStrList.get(index).trim();
            failed.add(failedTestCase);
            failedTestCase = failedTestCase.substring(failedTestCase.indexOf("-") + 1).trim();
            failedTestCasesStrList.add(failedTestCase);
            int colonIndex = failedTestCase.indexOf("::");
            if (colonIndex > 0) {
                failedTestCase = failedTestCase.substring(0, colonIndex);
            }
            if (!failedTestCasesList.contains(failedTestCase)) {
                this.failedTestCaseClasses += failedTestCase + " ";
                failedTestCasesList.add(failedTestCase);
            }
        }

        List<String> tempFailed = new ArrayList<>();
        tempFailed.addAll(this.failedTestStrList);
        tempFailed.removeAll(failed);
        // FIXME: Using defects4j command in Java code may generate some new failed-passing test cases.
        // We call them as fake failed-passing test cases.
        this.fakeFailedTestCasesList.addAll(tempFailed);
    }

    public List<SuspiciousPosition> readSuspiciousCodeFromFile() {
        File suspiciousFile = null;
        String suspiciousFilePath = "";
        if (this.suspCodePosFile == null) {
            suspiciousFilePath = Configuration.cachePath;
        } else {
            suspiciousFilePath = this.suspCodePosFile.getPath();
        }
        suspiciousFilePath += OSHelper.separator() + this.buggyProject + OSHelper.separator() + this.metric + ".txt";
        List<SuspiciousPosition> suspiciousCodeList = new ArrayList<>();
        suspiciousFile = new File(suspiciousFilePath);
        if (suspiciousFile.exists()) {
            suspiciousCodeList = FileHelper.readSuspiciousCodeFromFile(suspiciousFilePath, 1);
        } else {
            String susPath = this.path + OSHelper.separator() + this.buggyProject + OSHelper.separator() + this.metric + ".txt";
            suspiciousFile = new File(susPath);
            if (!suspiciousFile.exists()) {
                FL.faultLocalization(this.path, this.buggyProject, metric);
            }
            suspiciousCodeList = FileHelper.readSuspiciousCodeFromFile(susPath, Configuration.SUS_LIMIT);
        }
        if (suspiciousCodeList == null) return null;
        if (suspiciousCodeList.isEmpty()) return null;
        return suspiciousCodeList;
    }

//	public void preFixing (List<SuspiciousPosition> suspiciousPositions) {
//		for (SuspiciousPosition suspiciousPosition : suspiciousPositions) {
//			copySusFile(suspiciousPosition);
//		}
//	}

    public SuspCodeNode parseSuspiciousCode(SuspiciousPosition suspiciousCode, FolderNode folderNode) {
        SuspCodeNode scn = null;

        ClassNode classNode = folderNode.findClassByQualifiedName(suspiciousCode.classPath);
        StatementNode statementNode = classNode.findStatemmentByLine(classNode.getChildren(), suspiciousCode.lineNumber);

        if (statementNode != null) {
            MethodNode methodNode = classNode.findMethodNodeInFile(suspiciousCode.lineNumber);

            String suspiciousClassName = suspiciousCode.classPath;
            int buggyLine = suspiciousCode.lineNumber;

            log.info(suspiciousClassName + " ===" + buggyLine);
            if (suspiciousClassName.contains("$")) {
                suspiciousClassName = suspiciousClassName.substring(0, suspiciousClassName.indexOf("$"));
            }
            String suspiciousJavaFile = suspiciousClassName.replace(".", "/") + ".java";

            suspiciousClassName = suspiciousJavaFile.substring(0, suspiciousJavaFile.length() - 5).replace("/", ".");
            log.info("Copy back up file");
            File targetJavaFile = new File(FileUtils.getFileAddressOfJava(dp.srcPath, suspiciousClassName));
            File targetClassFile = new File(FileUtils.getFileAddressOfClass(dp.classPath, suspiciousClassName));
            File javaBackup = new File(FileUtils.tempJavaPath(suspiciousClassName, this.dataType + "/" + this.buggyProject));
            File classBackup = new File(FileUtils.tempClassPath(suspiciousClassName, this.dataType + "/" + this.buggyProject));
            if (!javaBackup.exists() && !classBackup.exists()) {
                try {
//                    if (javaBackup.exists()) javaBackup.delete();
//                    if (classBackup.exists()) classBackup.delete();
                    Files.copy(targetJavaFile.toPath(), javaBackup.toPath());
                    Files.copy(targetClassFile.toPath(), classBackup.toPath());
                    log.info("Copy back up file success!");
                } catch (IOException e) {
                    log.error("Not found class to copy");
//                e.printStackTrace();
                }
            }
            scn = new SuspCodeNode(statementNode, classNode, methodNode, javaBackup, classBackup, targetJavaFile, targetClassFile, suspiciousJavaFile);
        }
        return scn;
    }
//
//    protected void testGeneratedTokenPatches(List<PatchCandidate> patchCandidates) {
//        int size = Math.min(patchCandidates.size(), 30);
//        for (int i = 0; i < size; i++) {
//            PatchCandidate patch = patchCandidates.get(i);
//            patchId++;
//            // Insert the patch.
//            Board.SuspCodeNode scn = patch.getSuspiciousCode();
//            addPatchCodeToFile(scn, patch);
//            String buggyCode = scn.suspCodeAstNode.toString();
//            String patchCode = patch.toString();
//            scn.targetClassFile.delete();
//            log.info("");
//            log.info("===============================================================================");
//            log.info("[Candidate " + patchId + "/" +patchCandidates.size() + "] " + scn.buggyLine +"- "+  " -> " + patchCode.toString());
//            log.info("[Compiling]");
//            try {// Compile patched file.
//                System.out.println(Arrays.asList("javac -Xlint:unchecked -source 1.7 -target 1.7 -cp "
//                        + PathUtils.buildCompileClassPath(Arrays.asList(PathUtils.getJunitPath()), dp.classPath, dp.testClassPath)
//                        + " -d " + dp.classPath + " " + scn.targetJavaFile.getAbsolutePath()));
//                ShellUtils.shellRun(Arrays.asList("javac -Xlint:unchecked -source 1.7 -target 1.7 -cp "
//                        + PathUtils.buildCompileClassPath(Arrays.asList(PathUtils.getJunitPath()), dp.classPath, dp.testClassPath)
//                        + " -d " + dp.classPath + " " + scn.targetJavaFile.getAbsolutePath()), buggyProject);
//            } catch (IOException e) {
//                log.info(buggyProject + " ---Fixer: fix fail because of javac exception! ");
//                continue;
//            }
//            if (!scn.targetClassFile.exists()) { // fail to compile
//                int results = (this.buggyProject.startsWith("Closure") || this.buggyProject.startsWith("Time") || this.buggyProject.startsWith("Mockito")) ? TestUtils.compileProjectWithDefects4j(fullBuggyProjectPath, defects4jPath) : 1;
//                if (results == 1) {
//                    log.info(buggyProject + " ---Fixer: fix fail because of failed compiling! ");
//                    continue;
//                }
//            }
//            log.info("Finish of compiling.");
//
//            log.info("Test previously failed test cases.");
//            try {
//                log.info("[Running]");
//                System.out.println(Arrays.asList("java -cp "
//                        + PathUtils.buildTestClassPath(dp.classPath, dp.testClassPath)
//                        + " org.junit.runner.JUnitCore " + this.failedTestCaseClasses));
//                String results = ShellUtils.shellRun(Arrays.asList("java -cp "
//                        + PathUtils.buildTestClassPath(dp.classPath, dp.testClassPath)
//                        + " org.junit.runner.JUnitCore " + this.failedTestCaseClasses), buggyProject);
//
//                if (results.isEmpty()) {
//                    System.err.println(scn.suspiciousJavaFile + "@" + scn.buggyLine);
//                    System.err.println("Bug: " + buggyCode);
//                    System.err.println("Patch: " + patchCode);
//                    continue;
//                } else {
//                    if (!results.contains("java.lang.NoClassDefFoundError")) {
//                        List<String> tempFailedTestCases = readTestResults(results);
//                        tempFailedTestCases.retainAll(this.fakeFailedTestCasesList);
//                        if (!tempFailedTestCases.isEmpty()) {
//                            if (this.failedTestCasesStrList.size() == 1) continue;
//
//                            // Might be partially fixed.
//                            tempFailedTestCases.removeAll(this.failedTestCasesStrList);
//                            if (!tempFailedTestCases.isEmpty()) continue; // Generate new bugs.
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                log.info(buggyProject + " ---Fixer: fix fail because of faile passing previously failed test cases! ");
//                continue;
//            }
//            long start = System.nanoTime();
//            List<String> failedTestsAfterFix = new ArrayList<>();
//            int errorTestAfterFix = TestUtils.getFailTestNumInProject(fullBuggyProjectPath, this.defects4jPath,
//                    failedTestsAfterFix);
//            System.out.println("END TIME: " + (System.nanoTime() - start)/1000000);
//
//            failedTestsAfterFix.removeAll(this.fakeFailedTestCasesList);
//
//            if (errorTestAfterFix < minErrorTest) {
//                List<String> tmpFailedTestsAfterFix = new ArrayList<>();
//                tmpFailedTestsAfterFix.addAll(failedTestsAfterFix);
//                tmpFailedTestsAfterFix.removeAll(this.failedTestStrList);
//                if (tmpFailedTestsAfterFix.size() > 0) { // Generate new bugs.
//                    log.info(buggyProject + " ---Generated new bugs: " + tmpFailedTestsAfterFix.size());
//                    continue;
//                }
//
//                // Output the generated patch.
//                if (errorTestAfterFix == 0 || failedTestsAfterFix.isEmpty()) {
//                    fixedStatus = 1;
//                    log.info("Succeeded to fix the bug " + buggyProject + "====================");
//                    String patchStr = TestUtils.readPatch(this.fullBuggyProjectPath);
//                    if (patchStr == null || !patchStr.startsWith("diff")) {
//                        FileHelper.outputToFile(Runner.outPath+ "FixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt",
//                                "//**********************************************************\n//" + scn.suspiciousJavaFile + " ------ " + scn.buggyLine
//                                        + "\n//**********************************************************\n"
//                                        + "===Buggy Code===\n" + buggyCode + "\n\n===Patch Code===\n" + patchCode, false);
//                    } else {
//                        FileHelper.outputToFile(Runner.outPath+ "FixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt", patchStr + "\n", false);
//                    }
//                    this.minErrorTest = 0;
//                    break;
//                } else {
//                    if (minErrorTestAfterFix == 0 || errorTestAfterFix <= minErrorTestAfterFix) {
//                        minErrorTestAfterFix = errorTestAfterFix;
//                        if (fixedStatus != 1) fixedStatus = 2;
//                        log.info("Partially Succeeded to fix the bug " + buggyProject + "====================");
//                        String patchStr = TestUtils.readPatch(this.fullBuggyProjectPath);
//                        if (patchStr == null || !patchStr.startsWith("diff")) {
//                            FileHelper.outputToFile(Runner.outPath+ "PartiallyFixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt",
//                                    "//**********************************************************\n//" + scn.suspiciousJavaFile + " ------ " + scn.buggyLine
//                                            + "\n//**********************************************************\n"
//                                            + "===Buggy Code===\n" + buggyCode + "\n\n===Patch Code===\n" + patchCode, false);
//                        } else {
//                            FileHelper.outputToFile(Runner.outPath+ "PartiallyFixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt", patchStr + "\n", false);
//                        }
//                    }
//                }
//            } else {
//                log.info("Failed Tests after fixing: " + errorTestAfterFix + " " + buggyProject);
//            }
//            try {
//                scn.targetJavaFile.delete();
//                scn.targetClassFile.delete();
//                Files.copy(scn.javaBackup.toPath(), scn.targetJavaFile.toPath());
//                Files.copy(scn.classBackup.toPath(), scn.targetClassFile.toPath());
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//    }

    protected void testGeneratedTokenPatches(SortedSet<String> patchCandidates) throws IOException, InterruptedException, TimeoutException {
//        int size = Math.min(patchCandidates.size(), 30);
        Iterator<String> it = patchCandidates.iterator();
        String javaBackup = "";
        String targetJavaFile = "";
        String targetClassFile = "";
        String classBackup = "";
        String current = "";
        boolean isFix = false;
        while (it.hasNext() || !isFix) {
            current = it.next();
            String[] elements = current.split("`");
//            double score = Double.valueOf(elements[0]);
            String content = elements[1];
            int startPos = Integer.parseInt(elements[2]);
            int endPos = Integer.parseInt(elements[3]);
            javaBackup = elements[4];
            targetJavaFile = elements[5];
            targetClassFile = elements[6];
            classBackup = elements[7];

            patchId++;
            // Insert the patch.
            addPatchCodeToFile(javaBackup, startPos, endPos, content, targetJavaFile);
//            String buggyCode = scn.suspCodeAstNode.toString();
            String patchCode = content;
            if (Files.exists(Paths.get(targetClassFile))) {
                Files.delete(Paths.get(targetClassFile));
            }
            log.info("");
            log.info("===============================================================================");
            log.info("[Candidate " + patchId + "/" + patchCandidates.size() + "] " + " -> " + patchCode.toString());
            log.info("[Compiling]");
            try {// Compile patched file.
                System.out.println(Arrays.asList("javac -Xlint:unchecked -source 1.7 -target 1.7 -cp "
                        + PathUtils.buildCompileClassPath(Arrays.asList(PathUtils.getJunitPath()), dp.classPath, dp.testClassPath)
                        + " -d " + dp.classPath + " " + targetJavaFile));
                ShellUtils.shellRun(Arrays.asList("javac -Xlint:unchecked -source 1.7 -target 1.7 -cp "
                        + PathUtils.buildCompileClassPath(Arrays.asList(PathUtils.getJunitPath()), dp.classPath, dp.testClassPath)
                        + " -d " + dp.classPath + " " + targetJavaFile), buggyProject, false);
            } catch (IOException e) {
                log.info(buggyProject + " ---Fixer: fix fail because of javac exception! ");
                continue;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            if (!Files.exists(Paths.get(targetClassFile))) { // fail to compile
                int results = (this.buggyProject.startsWith("Closure") || this.buggyProject.startsWith("Time") || this.buggyProject.startsWith("Mockito")) ? TestUtils.compileProjectWithDefects4j(fullBuggyProjectPath, defects4jPath) : 1;
                if (results == 1) {
                    log.info(buggyProject + " ---Fixer: fix fail because of failed compiling! ");
                    continue;
                }
            }
            log.info("Finish of compiling.");

            log.info("Test previously failed test cases.");
            log.info("[Running]");
            System.out.println(Arrays.asList("java -cp "
                    + PathUtils.buildTestClassPath(dp.classPath, dp.testClassPath)
                    + " org.junit.runner.JUnitCore " + this.failedTestCaseClasses));


            String results = ShellUtils.shellRun(Arrays.asList("java -cp "
                    + PathUtils.buildTestClassPath(dp.classPath, dp.testClassPath)
                    + " org.junit.runner.JUnitCore " + this.failedTestCaseClasses), buggyProject, true);
            if (results.isEmpty()) {
                System.err.println("Bug: ");
                System.err.println("Patch: " + patchCode);
                continue;
            } else {
                if (!results.contains("java.lang.NoClassDefFoundError")) {
                    List<String> tempFailedTestCases = readTestResults(results);
                    tempFailedTestCases.retainAll(this.fakeFailedTestCasesList);
                    if (!tempFailedTestCases.isEmpty()) {
                        if (this.failedTestCasesStrList.size() == 1) continue;

                        // Might be partially fixed.
                        tempFailedTestCases.removeAll(this.failedTestCasesStrList);
                        if (!tempFailedTestCases.isEmpty()) continue; // Generate new bugs.
                    }
                }
            }
            long start = System.nanoTime();
            List<String> failedTestsAfterFix = new ArrayList<>();
            int errorTestAfterFix = TestUtils.getFailTestNumInProject(fullBuggyProjectPath, this.defects4jPath,
                    failedTestsAfterFix);
            System.out.println("END TIME: " + (System.nanoTime() - start) / 1000000);

            failedTestsAfterFix.removeAll(this.fakeFailedTestCasesList);

            if (errorTestAfterFix < minErrorTest) {
                List<String> tmpFailedTestsAfterFix = new ArrayList<>();
                tmpFailedTestsAfterFix.addAll(failedTestsAfterFix);
                tmpFailedTestsAfterFix.removeAll(this.failedTestStrList);
                if (tmpFailedTestsAfterFix.size() > 0) { // Generate new bugs.
                    log.info(buggyProject + " ---Generated new bugs: " + tmpFailedTestsAfterFix.size());
                    continue;
                }

                // Output the generated patch.
                if (errorTestAfterFix == 0 || failedTestsAfterFix.isEmpty()) {
                    fixedStatus = 1;
                    log.info("Succeeded to fix the bug " + buggyProject + "====================");
                    String patchStr = TestUtils.readPatch(this.fullBuggyProjectPath);
                    if (patchStr == null || !patchStr.startsWith("diff")) {
                        FileHelper.outputToFile(Configuration.outputPatches + "/FixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt",
                                "//**********************************************************\n//" + targetJavaFile + " ------ "
                                        + "\n//**********************************************************\n"
                                        + "===Buggy Code===\n" + "buggyCode" + "\n\n===Patch Code===\n" + patchCode, false);
                    } else {
                        FileHelper.outputToFile(Configuration.outputPatches + "/FixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt", patchStr + "\n", false);
                    }
                    this.minErrorTest = 0;
                    isFix = true;
                    break;
                } else {
                    if (minErrorTestAfterFix == 0 || errorTestAfterFix <= minErrorTestAfterFix) {
                        minErrorTestAfterFix = errorTestAfterFix;
                        if (fixedStatus != 1) fixedStatus = 2;
                        log.info("Partially Succeeded to fix the bug " + buggyProject + "====================");
                        String patchStr = TestUtils.readPatch(this.fullBuggyProjectPath);
                        if (patchStr == null || !patchStr.startsWith("diff")) {
                            FileHelper.outputToFile(Configuration.outputPatches+ "/PartiallyFixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt",
                                    "//**********************************************************\n//" + " ------ "
                                            + "\n//**********************************************************\n"
                                            + "===Buggy Code===\n" + "\n\n===Patch Code===\n" + patchCode, false);
                        } else {
                            FileHelper.outputToFile(Configuration.outputPatches + "/PartiallyFixedBugs/" + buggyProject + "/Patch_" + patchId + ".txt", patchStr + "\n", false);
                        }
                    }
                }
            } else {
                log.info("Failed Tests after fixing: " + errorTestAfterFix + " " + buggyProject);
            }
            try {
                Files.delete(Paths.get(targetJavaFile));
                Files.delete(Paths.get(targetClassFile));
                Files.copy(Paths.get(javaBackup), Paths.get(targetJavaFile));
                Files.copy(Paths.get(classBackup), Paths.get(targetClassFile));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private List<String> readTestResults(String results) {
        List<String> failedTeatCases = new ArrayList<>();
        String[] testResults = results.split("\n");
        for (String testResult : testResults) {
            if (testResult.isEmpty()) continue;

            if (NumberUtils.isDigits(testResult.substring(0, 1))) {
                int index = testResult.indexOf(") ");
                if (index <= 0) continue;
                testResult = testResult.substring(index + 1, testResult.length() - 1).trim();
                int indexOfLeftParenthesis = testResult.indexOf("(");
                if (indexOfLeftParenthesis < 0) {
                    System.err.println(testResult);
                    continue;
                }
                String testCase = testResult.substring(0, indexOfLeftParenthesis);
                String testClass = testResult.substring(indexOfLeftParenthesis + 1);
                failedTeatCases.add(testClass + "::" + testCase);
            }
        }
        return failedTeatCases;
    }

    private String addPatchCodeToFile(SuspCodeNode scn, PatchCandidate patch) {
        String javaCode = FileHelper.readFile(scn.javaBackup);

        int exactBuggyCodeStartPos = patch.getTargetNode().getStartPostion();
        int exactBuggyCodeEndPos = patch.getTargetNode().getEndPostion();
        String patchedJavaFile = javaCode.substring(0, exactBuggyCodeStartPos) + patch + javaCode.substring(exactBuggyCodeEndPos);
        File newFile = null;
        try {
            newFile = new File(scn.targetJavaFile.getAbsolutePath() + ".temp");
            FileHelper.outputToFile(newFile, patchedJavaFile, false);
            newFile.renameTo(scn.targetJavaFile);
        } catch (StringIndexOutOfBoundsException e) {
            log.info(exactBuggyCodeStartPos + " ==> " + exactBuggyCodeEndPos + " : " + javaCode.length());
            e.printStackTrace();
        }
        return patchedJavaFile;

    }

    private String addPatchCodeToFile(String javaBackup, int startPos, int endPos, String content, String targetJavaFile) {
        String javaCode = FileHelper.readFile(javaBackup);

        int exactBuggyCodeStartPos = startPos;
        int exactBuggyCodeEndPos = endPos;
        String patchedJavaFile = javaCode.substring(0, exactBuggyCodeStartPos) + content + javaCode.substring(exactBuggyCodeEndPos);
        File newFile = null;
        try {
            newFile = new File(targetJavaFile + ".temp");
            FileHelper.outputToFile(newFile, patchedJavaFile, false);
            newFile.renameTo(new File(targetJavaFile));
        } catch (StringIndexOutOfBoundsException e) {
            log.info(exactBuggyCodeStartPos + " ==> " + exactBuggyCodeEndPos + " : " + javaCode.length());
            e.printStackTrace();
        }
        return patchedJavaFile;

    }

    private String addPatchCodeToFile(SuspCodeNode scn, TokenPatch patch) {
        String javaCode = FileHelper.readFile(scn.javaBackup);

        int exactBuggyCodeStartPos = scn.startPos;
        int exactBuggyCodeEndPos = scn.endPos;
        String patchedJavaFile = javaCode.substring(0, exactBuggyCodeStartPos) + patch + javaCode.substring(exactBuggyCodeEndPos);
        File newFile = null;
        try {

            newFile = new File(scn.targetJavaFile.getAbsolutePath() + ".temp");
            FileHelper.outputToFile(newFile, patchedJavaFile, false);
            newFile.renameTo(scn.targetJavaFile);
        } catch (StringIndexOutOfBoundsException e) {
            log.info(exactBuggyCodeStartPos + " ==> " + exactBuggyCodeEndPos + " : " + javaCode.length());
            e.printStackTrace();
        }
        return patchedJavaFile;
    }

    private String generateJavaFile(TokenPatch patch, String javaCode, String buggyCode) {
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

    public class SuspCodeNode {

        public File javaBackup;
        public File classBackup;
        public File targetJavaFile;
        public File targetClassFile;
        public int startPos;
        public int endPos;
        public StatementNode suspCodeAstNode;
        public String suspCodeStr;
        public ClassNode classNode;
        public MethodNode methodNode;
        public String suspiciousJavaFile;
        public int buggyLine;

        public SuspCodeNode(StatementNode statementNode, ClassNode classNode, MethodNode methodNode, File javaBackup, File classBackup, File targetJavaFile, File targetClassFile, String filename) {
            this.javaBackup = javaBackup;
            this.classBackup = classBackup;
            this.targetJavaFile = targetJavaFile;
            this.targetClassFile = targetClassFile;
            this.startPos = statementNode.getStartPostion();
            this.endPos = statementNode.getEndPostion();
            this.suspCodeAstNode = statementNode;
            this.suspCodeStr = statementNode.toString();
            this.suspiciousJavaFile = filename;
            this.buggyLine = statementNode.getLine();
            this.classNode = classNode;
            this.methodNode = methodNode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof SuspCodeNode) {
                SuspCodeNode suspN = (SuspCodeNode) obj;
                if (startPos != suspN.startPos) return false;
                if (endPos != suspN.endPos) return false;
                if (suspiciousJavaFile.equals(suspN.suspiciousJavaFile)) return true;
            }
            return false;
        }
    }
}
