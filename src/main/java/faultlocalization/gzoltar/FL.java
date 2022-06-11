package faultlocalization.gzoltar;

import config.Configuration;

import java.io.File;

public class FL {

    /**
     * Input:
     * 1. Defects4J project path. e.g., ../Defects4JData/
     * 2. Output path: e.g., suspiciousCodePositions/
     * 3. The project name: e.g., Chart_1
     * <p>
     * Output:
     * 1. a ranked list of suspicious statements for a buggy project.
     *
     * @param args
     */
    public static void main(String[] args) {
        String outputPath = Configuration.suspPositionsFilePath;//args[0]; // Configuration.SUSPICIOUS_POSITIONS_FILE_APTH;

        //=====Test for student=======
//        String path = Configuration.OASIS_PROJECT_PATH;
//        String projectName = "80776";//args[2]; project test name for student
//        String projectName = "83918";//args[2]; project test name for student
        //=====End test for student

        //=====Teclass st for Project =====
        String path = Configuration.BUGGY_PROJECTS_PATH;//args[1]; // Configuration.BUGGY_PROJECTS_PATH;
//        String projectName = "Chart_11_buggy";//args[2]; project test name for project
        String projectName = "Time_19";//args[2]; project test name for project
        //=====End test for project

        String metricStr = Configuration.faultLocalizationMetric; //caculator dupplicate

        if (!projectName.contains("_")) {
            //Every thing in a folder
            StudentFaultLocalization st = new StudentFaultLocalization();
            st.locateSuspiciousCode(path, projectName, outputPath, metricStr);
        } else {
            ProjectLocalization projectLocalization = new ProjectLocalization();
            projectLocalization.locateSuspiciousCode(path, projectName, outputPath, metricStr);
        }
    }

    public static Object faultLocalization(String projectPath, String projectName, String metric) {
//        String outputPath = Configuration.suspPositionsFilePath;//args[0]; // Configuration.SUSPICIOUS_POSITIONS_FILE_APTH;
        String outputPath = projectPath;//args[0]; // Configuration.SUSPICIOUS_POSITIONS_FILE_APTH;
        String path = projectPath + File.separator;

        String metricStr = metric; //caculator dupplicate

        if (!projectName.contains("_")) {
            //Every thing in a folder
            StudentFaultLocalization st = new StudentFaultLocalization();
            st.locateSuspiciousCode(path, projectName, outputPath, metricStr);
            return st;
        } else {
            ProjectLocalization projectLocalization = new ProjectLocalization();
            projectLocalization.locateSuspiciousCode(path, projectName, outputPath, metricStr);
            return projectLocalization;
        }
    }


}
