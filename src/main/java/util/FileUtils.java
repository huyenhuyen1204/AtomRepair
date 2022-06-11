package util;

import config.Configuration;

import java.io.File;

public class FileUtils {
    public static String getFileAddressOfJava(String srcPath, String className) {
        if (className.contains("<") && className.contains(">")) {
            className = className.substring(0, className.indexOf("<"));
        }
        return srcPath.trim() + System.getProperty("file.separator")
                + className.trim().replace('.', System.getProperty("file.separator").charAt(0)) + ".java";
    }

    public static String getFileAddressOfClass(String classPath, String className) {
        if (className.contains("<") && className.contains(">")) {
            className = className.substring(0, className.indexOf("<"));
        }
        return classPath.trim() + System.getProperty("file.separator")
                + className.trim().replace('.', System.getProperty("file.separator").charAt(0)) + ".class";
    }

    public static String tempJavaPath(String classname, String identifier) {
        new File(Configuration.TEMP_FILES_PATH + identifier).mkdirs();
        return Configuration.TEMP_FILES_PATH + identifier + "/" + classname.substring(classname.lastIndexOf(".") + 1) + ".java";
    }

    public static String tempClassPath(String classname, String identifier) {
        new File(Configuration.TEMP_FILES_PATH + identifier).mkdirs();
        return Configuration.TEMP_FILES_PATH + identifier + "/" + classname.substring(classname.lastIndexOf(".") + 1) + ".class";
    }
}
