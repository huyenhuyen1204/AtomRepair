package util;

import java.io.File;

public class OSHelper {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static String separator() {
        return File.separator;
    }
    public static String delimiter () {
        if (OS.indexOf("win") >= 0) {
            return ";";
        } else if (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0) {
            return ":";
        } else {
            return ";";
        }
    }

}
