package config;

public class Configuration {
	
	/*
	 * Data path of Defects4J bugs.
	 * and finish with '\' char
	 * example: C:\Users\Dell\Desktop\APRresearch\Test\
	 */
//	public static final String BUGGY_PROJECTS_PATH = "/home/huyenhuyen/Desktop/tools/capgenimpl/GZoltar/";
//	public static final String BUGGY_PROJECTS_PATH = "/home/huyenhuyen/Desktop/benmarks/";
	public static final String BUGGY_PROJECTS_PATH = "/home/huyenhuyen/Desktop/APR/benmarks/";
	public static final String TEMP_FILES_PATH = ".temp/";
//	public static final long SHELL_RUN_TIMEOUT = 10800L;
	public static final long SHELL_RUN_TIMEOUT = 600L;
	public static int SUS_LIMIT = 36;


	public static String knownBugPositions = "BugPositions.txt";
	public static String suspPositionsFilePath = "output/suspicious-code-positions";
	public static String cachePath = ".cache";
	public static String failedTestCasesFilePath = "FailedTestCases/";
	public static String faultLocalizationMetric = "Ochiai";
	public static String outputPatches = "output/patches";
	public static String nodePath = "data/node.csv";


	//FOR OASIS
	public static final String OASIS_PROJECT_PATH ="/home/huyenhuyen/Desktop/HAPR/data_test/";

}