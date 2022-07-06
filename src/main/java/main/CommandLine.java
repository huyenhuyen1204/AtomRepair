//package main;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.concurrent.Callable;
//
//@picocli.CommandLine.Command(
//        name = "atom-repair",
//        description = "Automated Program Repair",
//        version = "0.4-ALPHA",
//        mixinStandardHelpOptions = true,
//        exitCodeOnExecutionException = -1,
//        exitCodeOnInvalidInput = 1
//)
//public class CommandLine implements Callable<Void> {
//
//
//    @picocli.CommandLine.Option(
//            names = {"-b", "--bug"},
//            paramLabel = "<bug_name>",
//            required = true,
//            description = "Bug name.(eg: Chart_1)"
//    )
//    private String bug;
//
//    @picocli.CommandLine.Option(
//            names = {"-w", "--work"},
//            paramLabel = "<working_folder>",
//            required = true,
//            description = "Path to working folder."
//    )
//    private Path workingFolder;
//
//    @picocli.CommandLine.Option(
//            names = {"-df", "--dfpath"},
//            paramLabel = "<defects4j_path>",
//            required = true,
//            description = "Defects4J path"
//    )
//    private Path dfPath;
//
//    @Override
//    public Void call() throws Exception {
//        Runner.main(bug, workingFolder, dfPath);
//        return null;
//    }
//
//    public static void main(String[] args) {
//        new picocli.CommandLine(new CommandLine()).execute(args);
//    }
//}
