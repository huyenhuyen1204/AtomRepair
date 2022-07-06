package main;

import AST.node.FolderNode;
import AST.parser.ProjectParser;
import config.Configuration;
import faultlocalization.objects.SuspiciousPosition;
import junit.framework.TestCase;
import util.ReflectionHelper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RunnerTest extends TestCase {

    public void test1() throws IOException, InterruptedException, TimeoutException {
        String projectPath = ".temp/";
//        String projectPath = "C:\\Users\\Dell\\Desktop\\APR_Research\\Tools\\CapGen\\Defects4J\\";
        String projectName = "Closure_1";
        String Defects4J_PATH = "/home/huyenhuyen/Desktop/docker-data/defects4j/";
        Configuration.suspPositionsFilePath = ".temp/";
        Board board = new Board(projectPath, projectName, Defects4J_PATH, false);
        List<SuspiciousPosition> suspiciousPositions = board.readSuspiciousCodeFromFile();
        ReflectionHelper.initClassLoader( board.fullBuggyProjectPath);
        board.setRunTest(false);

        FolderNode folderNode = ProjectParser.parse(board.dp.srcPath);
        long start = System.nanoTime();
        Fix.fixProcess(suspiciousPositions, folderNode, board);
        long elapsedTime = System.nanoTime() - start;
        System.out.println("TOTAL TIME: " + elapsedTime/1000000000);
    }
}