package AST.parser;

import AST.node.ClassNode;
import AST.node.FolderNode;
import util.FileService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProjectParser {
    public FolderNode folderNode = new FolderNode();

    public static FolderNode parse(String rootPath) throws IOException {
        ProjectParser projectParser = new ProjectParser();
        projectParser.doParsing(rootPath, 0);
        projectParser.folderNode.addTypeForMethodInvocations();
        return projectParser.folderNode;
    }

    public FolderNode getFolderNode() {
        return folderNode;
    }

    public void setFolderNode(FolderNode folderNode) {
        this.folderNode = folderNode;
    }

    public void doParsing(String path, int level) throws IOException {
        FolderNode folderNode = this.getFolderNode();
        File mainDir = new File(path);
        if (mainDir.exists() && mainDir.isDirectory()) {
            // array for files and sub-directories
            // of directory pointed by mainDir
            File[] arr = mainDir.listFiles();

            if (Objects.isNull(arr)) {
                return;
            }

            // for-each loop for main directory files
            for (File f : arr) {
                if (f.isFile() && f.getName().endsWith(".java")) {
                    String fileToString = FileService.readFileToString(f.getPath());
                    List<ClassNode> classes = JavaFileParser.parse(f, fileToString);
                    folderNode.addChildrenFolder(classes);

                }
                else if (f.isDirectory()) {
                    FolderNode childFolder = new FolderNode();
                    childFolder.setName(f.getName());
                    ProjectParser projectParser = new ProjectParser();
                    projectParser.setFolderNode(childFolder);
                    projectParser.doParsing(f.getPath(), level + 1);
                    folderNode.getChildren().add(childFolder);
                }
            }
        }
    }


//    public static void main(String[] args) throws IOException {
//        FolderNode folderNode = ProjectParser.parse("C:\\Users\\Dell\\Desktop\\APR_Test\\data_test\\82260");
//        File file = new File()
//        FileHelper.createFile();
//        System.out.println(folderNode.toString());
//    }


}
