package faultlocalization.dataprepare;


import util.FileHelper;
import util.JavaLibraryHelper;
import util.OSHelper;
import util.PathUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Prepare data for fault localization, program compiling and testing.
 * 
 * @author kui.liu
 *
 */
public class DataPreparer {

    private String buggyProjectParentPath;
    
    public String classPath;
    public String srcPath;
    public String testClassPath;
    public String testSrcPath;
    public String libPath;
    public List<String> libPaths = new ArrayList<>();
    public boolean validPaths = true;
    public String[] testCases;
    public URL[] classPaths;
    
    public DataPreparer(String path){
        if (!path.endsWith(File.separator)){
            path += File.separator;
        }
        buggyProjectParentPath = path;
    }
    
    public void prepareData(String buggyProject){

		loadPaths(buggyProject);
		
		if (!checkProjectDirectories()){
			validPaths = false;
			return;
		}
		
		loadTestCases();
		
		loadClassPaths();
    }

	private void loadPaths(String buggyProject) {
		String projectDir = buggyProjectParentPath;
		//Get path with project name
		List<String> paths = PathUtils.getSrcPath(buggyProject);
		classPath = projectDir + buggyProject + paths.get(0);
		testClassPath = projectDir + buggyProject + paths.get(1);
		srcPath = projectDir + buggyProject + paths.get(2);
		testSrcPath = projectDir + buggyProject + paths.get(3);
		libPath = projectDir + buggyProject + paths.get(4);

		List<File> libPackages = new ArrayList<>();
		if (new File(projectDir + buggyProject + "/lib/").exists()) {
			libPackages.addAll(FileHelper.getAllFiles(projectDir + buggyProject + "/lib/", ".jar"));
		}
		if (new File(projectDir + buggyProject + "/build/lib/").exists()) {
			libPackages.addAll(FileHelper.getAllFiles(projectDir + buggyProject + "/build/lib/", ".jar"));
		}
		for (File libPackage : libPackages) {
			libPaths.add(libPackage.getAbsolutePath());
		}
	}
	
	private boolean checkProjectDirectories() {
		if (!new File(classPath).exists()) {
			System.err.println("Class path: " + classPath + " does not exist!");
			return false;
		}
		if (!new File(srcPath).exists()) {
			System.err.println("Source code path: " + srcPath + " does not exist!");
			return false;
		}
		if (!new File(testClassPath).exists()) {
			System.err.println("Test class path: " + testClassPath + " does not exist!");
			return false;
		}
		if (!new File(testSrcPath).exists()) {
			System.err.println("Test source path: " + testSrcPath + " does not exist!");
			return false;
		}
		return true;
	}

	private void loadTestCases() {
		testCases = new TestClassesFinder().findIn(JavaLibraryHelper.classPathFrom(testClassPath + OSHelper.delimiter() + classPath), false);
		Arrays.sort(testCases);
	}

	private void loadClassPaths() {
		classPaths = JavaLibraryHelper.classPathFrom(testClassPath);
		classPaths = JavaLibraryHelper.extendClassPathWith(classPath, classPaths);
		if (libPaths != null) {
			for (String lpath : libPaths) {
				classPaths = JavaLibraryHelper.extendClassPathWith(lpath, classPaths);
			}
		}
	}

	// ========== FOR OASIS =========
	public void prepareOASISData(String buggyProject) {
		loadOASISPaths(buggyProject);

		if (!checkProjectDirectories()){
			validPaths = false;
			return;
		}

		loadTestCases();
		loadClassPaths();
//		loadOASISClassPaths();
	}

	private void loadOASISTestCases() {
	}

	private void loadOASISPaths(String buggyProject) {
		String projectDir = buggyProjectParentPath;
		//Get path with project name
		List<String> paths = PathUtils.getOASISSrcPath(buggyProject);
		classPath = projectDir + buggyProject + paths.get(0);
		testClassPath = projectDir + buggyProject + paths.get(1);
		srcPath = projectDir + buggyProject + paths.get(2);
		testSrcPath = projectDir + buggyProject + paths.get(3);

//		List<File> libPackages = new ArrayList<>();
//		if (new File(projectDir + buggyProject + "/lib/").exists()) {
//			libPackages.addAll(FileHelper.getAllFiles(projectDir + buggyProject + "/lib/", ".jar"));
//		}
//		if (new File(projectDir + buggyProject + "/build/lib/").exists()) {
//			libPackages.addAll(FileHelper.getAllFiles(projectDir + buggyProject + "/build/lib/", ".jar"));
//		}
//		for (File libPackage : libPackages) {
//			libPaths.add(libPackage.getAbsolutePath());
//		}

	}
}
