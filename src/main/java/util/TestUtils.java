package util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TestUtils {


	public static int getFailTestNumInProject(String projectName, String defects4jPath, List<String> failedTests){
        String testResult = getDefects4jResult(projectName, defects4jPath, "test");
        if (testResult.equals("")){//error occurs in run
            return Integer.MAX_VALUE;
        }
        if (!testResult.contains("Failing tests:")){
            return Integer.MAX_VALUE;
        }
        int errorNum = 0;
        String[] lines = testResult.trim().split("\n");
        for (String lineString: lines){
            if (lineString.startsWith("Failing tests:")){
                errorNum =  Integer.valueOf(lineString.split(":")[1].trim());
                if (errorNum == 0) break;
            } else if (lineString.startsWith("Running ")) {
            	break;
            } else {
            	failedTests.add(lineString.trim());
            }
        }
        return errorNum;
	}
	
//	public static int getFailTestNumInProject(String buggyProject, List<String> failedTests, String classPath,
//			String testClassPath, String[] testCasesArray){
//		StringBuilder builder = new StringBuilder();
//		for (String testCase : testCasesArray) {
//			builder.append(testCase).append(" ");
//		}
//		String testCases = builder.toString();
//		
//		String testResult = "";
//		try {
//			testResult = ShellUtils.shellRun(Arrays.asList("java -cp " + PathUtils.buildClassPath(classPath, testClassPath)
//					+ " org.junit.runner.JUnitCore " + testCases), buggyProject);
//		} catch (IOException e) {
////			e.printStackTrace();
//		}
//		
//        if (testResult.equals("")){//error occurs in run
//            return Integer.MAX_VALUE;
//        }
//        if (!testResult.contains("Failing tests:")){
//            return Integer.MAX_VALUE;
//        }
//        int errorNum = 0;
//        String[] lines = testResult.trim().split("\n");
//        for (String lineString: lines){
//            if (lineString.startsWith("Failing tests:")){
//                errorNum =  Integer.valueOf(lineString.split(":")[1].trim());
//                if (errorNum == 0) break;
//            } else if (lineString.startsWith("Running ")) {
//            	break;
//            } else {
//            	failedTests.add(lineString);
//            }
//        }
//        return errorNum;
//	}
	
	public static int compileProjectWithDefects4j(String projectName, String defects4jPath) {
		String compileResults = getDefects4jResult(projectName, defects4jPath, "compile");
		String[] lines = compileResults.split("\n");
		if (lines.length != 2) return 1;
        for (String lineString: lines){
        	if (!lineString.endsWith("OK")) return 1;
        }
		return 0;
	}

    public static int cloneProjectWithDefects4j(String projectName, String defects4jPath) {
        String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
        String[] ids = buggyProject.split("_");
        String cmd = defects4jPath + "framework/bin/defects4j checkout -p "+ids[0]+" -v "+ids[1]+"b -w " + projectName;
        String compileResults = cloneDefects4jResult(projectName, defects4jPath, cmd);
        return 0;
    }

	private static String getDefects4jResult(String projectName, String defects4jPath, String cmdType) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            System.out.println("GetDefects4jResult: " + Arrays.asList("cd " + projectName + "\n", defects4jPath + "framework/bin/defects4j " + cmdType + "\n"));
			//which java\njava -version\n
            String result = ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", defects4jPath + "framework/bin/defects4j " + cmdType + "\n"), buggyProject, false);//"defects4j " + cmdType + "\n"));//
            return result.trim();
        } catch (IOException e){
        	e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String cloneDefects4jResult(String projectName, String defects4jPath, String cmdType) {
        try {
            String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            System.out.println("GetDefects4jResult: " + Arrays.asList( cmdType + "\n", "cd " + projectName + "\n"));
            //which java\njava -version\n
            String result = ShellUtils.shellRun(Arrays.asList(cmdType + "\n", "cd " + projectName + "\n"), buggyProject, false);//"defects4j " + cmdType + "\n"));//
            return result.trim();
        } catch (IOException e){
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return "";
    }

	public static String recoverWithGitCmd(String projectName) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git checkout -- ."), buggyProject, false);
            return "";
        } catch (IOException e){
            return "Failed to recover.";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return "";
    }

	public static String readPatch(String projectName) {
		try {
			String buggyProject = projectName.substring(projectName.lastIndexOf("/") + 1);
            return ShellUtils.shellRun(Arrays.asList("cd " + projectName + "\n", "git diff"), buggyProject, false).trim();
        } catch (IOException e){
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return "";
    }

}
