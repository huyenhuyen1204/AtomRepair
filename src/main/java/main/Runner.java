package main;

import AST.node.FolderNode;
import AST.parser.ProjectParser;
import faultlocalization.objects.SuspiciousPosition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.OSHelper;
import util.ReflectionHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Runner {
    private static Logger log = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        if (args.length != 3) {
            System.out.println("Arguments: <Bug> <Working_Folder> <defects4j_Path>");
            System.exit(0);
        }
        String bugName = args[0];
        String workFolder = args[1];
        String defects4J = args[2];
//        String projectPath = "/home/huyenhuyen/Desktop/APR/benmarks/";
//        String                                                                                                                                                                               projectPath = "data/";
//        String projectPath = "data/";
//        String Defects4J_PATH = "/home/huyenhuyen/Desktop/APR/defects4j/";
//        String Defects4J_PATH = "C:\\Users\\Dell\\Desktop\\APR_Research\\defects4j";

        Board board = new Board(workFolder + OSHelper.separator(), bugName,
                defects4J + OSHelper.separator(), true);
        List<SuspiciousPosition> suspiciousPositions = board.readSuspiciousCodeFromFile();
        if (suspiciousPositions != null) {
            ReflectionHelper.initClassLoader(board.fullBuggyProjectPath);
            long start = System.nanoTime();
            log.info("Starting parsing");
            FolderNode folderNode = ProjectParser.parse(board.dp.srcPath);
            log.info("Parsing success!");
            long elapsedTime = System.nanoTime() - start;
            System.out.println("TOTAL PARSER TIME : " + elapsedTime / 1000000000);
            //add type to Node:
            log.info("Starting fixing");
            Fix.fixProcess(suspiciousPositions, folderNode, board);
        } else {
            log.error("Not found suspicious positions");
        }
    }
}

