package faultlocalization.gzoltar;

import faultlocalization.abstracts.FL;
import faultlocalization.dataprepare.DataPreparer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileHelper;
import util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * For project has lib, classes, src and test
 */
public class ProjectLocalization extends FL {
    private static Logger log = LoggerFactory.getLogger(ProjectLocalization.class);

    public void locateSuspiciousCode(String path, String buggyProject, String outputPath, String metricStr) {
        if (!buggyProject.contains("_")) {
            System.out.println("Main: cannot recognize project name \"" + buggyProject + "\"");
            return;
        }

        String[] elements = buggyProject.split("_");
        try {
            Integer.valueOf(elements[1]);
        } catch (NumberFormatException e) {
            System.out.println("Main: cannot recognize project name \"" + buggyProject + "\"");
            return;
        }

        System.out.println(buggyProject);

        if (dp == null) {
            dp = new DataPreparer(path);
            dp.prepareData(buggyProject);
        }
        if (!dp.validPaths) return;

        GZoltarFaultLoclaization gzfl = new GZoltarFaultLoclaization();
        gzfl.threshold = 0.0;
        gzfl.maxSuspCandidates = -1;
        gzfl.srcPath = path + buggyProject + PathUtils.getSrcPath(buggyProject).get(2);

        try {
            gzfl.localizeSuspiciousCodeWithGZoltar(dp.classPaths, checkNotNull(Arrays.asList("")), path, dp.testCases);
        } catch (NullPointerException e) {
            log.error(buggyProject + "\n" + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println(metricStr);
        Metrics.Metric metric = new Metrics().generateMetric(metricStr);
        gzfl.sortSuspiciousCode(metric);

        suspStmts = new ArrayList<SuspiciousCode>(gzfl.candidates.size());
        suspStmts.addAll(gzfl.candidates);

        StringBuilder builder = new StringBuilder();
        for (int index = 0, size = suspStmts.size(); index < size; index ++) {
            SuspiciousCode candidate = suspStmts.get(index);
            String className = candidate.getClassName();
            int lineNumber = candidate.lineNumber;
            builder.append(className).append("@").append(lineNumber)
                    .append("@").append(candidate.getSuspiciousValueString()).append("\n");
        }
        FileHelper.outputToFile(outputPath + buggyProject + File.separator + metricStr + ".txt", builder, false);
    }
}
