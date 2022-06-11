package faultlocalization.abstracts;

import faultlocalization.dataprepare.DataPreparer;
import faultlocalization.gzoltar.SuspiciousCode;

import java.util.List;

public abstract class FL {
    public DataPreparer dp = null;
    public List<SuspiciousCode> suspStmts;
    public void locateSuspiciousCode(String path, String buggyProject, String outputPath, String metricStr) {};
}
