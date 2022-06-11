package util;

import java.io.File;

public class GuiHelper {

    /**
     * Return color
     * @param ratio
     * @param color
     * @return
     */
    public static String getColor(float ratio, String color) {
        String colorCode = "";
        if (color.equals("red")) {
            ratio = Float.compare(ratio, 1) == 0 ? 0.9f : ratio;
            float rate = 1f - ratio;
            float gbColor = rate * 255;
            gbColor = Math.round(gbColor);
            colorCode =  "rgba(255, "+ (int) gbColor + ", " + (int) gbColor + ", 0.85)";
        } else if (color.equals("green")) {

        } else if (color.equals("yellow")) {

        }
        System.out.println(colorCode);
        return colorCode;
    }

    /**
     * Write style of fault localization to hightlightFL.css
     * @param file
     * @param
     */
    public static void writeStyleClassToStyleFL(File file, float ratio, String color) {
        String ratioName = String.valueOf(ratio).replace(".", "_");
        String hightLightColor = "\"" +GuiHelper.getColor(ratio, color) +"\"";

        String content = ".fl"  + ratioName + " {"+ System.lineSeparator()
                + "    -rtfx-background-color: " + hightLightColor +";"+ System.lineSeparator() +"}"
                + System.lineSeparator();
        if(!FileHelper.isStringInFile(file, ".fl" + ratioName)) {
            FileHelper.addToFile(file, content);
        }
    }
}
