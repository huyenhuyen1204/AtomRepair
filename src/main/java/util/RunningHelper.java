package util;

import common.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class RunningHelper {
    public static final Logger logger = LoggerFactory.getLogger(RunningHelper.class);

    public static void compileFolder(String pathToSource, String pathToOutClass) throws IOException {
//        JDBHelper.setUTF8();
        //TODO: need edit here when compile Project
        String cmd = "javac -J-Dfile.encoding=UTF-8 -g -d " + pathToOutClass +
                " -cp " + Configure.APR_JAR_LIB + File.separator + "junit-4.12.jar" + OSHelper.delimiter() + Configure.APR_JAR_LIB + File.separator + "oasis-junit-1.0.jar"
                + " -sourcepath " + pathToSource + " MyTest.java TestRunner.java";
        logger.info(cmd);
        Runtime.getRuntime().exec(cmd, null, new File(pathToSource));
    }

    public static void runFolder(String pathToSource, String className) throws IOException {
//        JDBHelper.setUTF8();
        String hamscore = Configure.APR_JAR_LIB + OSHelper.separator() + "hamcrest-core-1.3.jar";
        String cmd = "java" +
                " -cp " + Configure.APR_JAR_LIB + File.separator + "tools.jar" + OSHelper.delimiter() +
                Configure.APR_JAR_LIB + File.separator + "junit-4.12.jar" + OSHelper.delimiter() +hamscore + OSHelper.delimiter() + Configure.APR_JAR_LIB + File.separator + "oasis-junit-1.0.jar" + OSHelper.delimiter() + ". " + className;
        logger.info(cmd);
        Runtime.getRuntime().exec(cmd, null, new File(pathToSource));
    }

    public static void setUTF8() throws IOException {
//        System.setProperty("file.encoding", "UTF-8");
//        String cmd = "export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8";
//        logger.info(cmd);
//        Runtime.getRuntime().exec(cmd);
    }
}
