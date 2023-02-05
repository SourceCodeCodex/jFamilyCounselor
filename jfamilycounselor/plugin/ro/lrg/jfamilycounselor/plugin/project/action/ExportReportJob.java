package ro.lrg.jfamilycounselor.plugin.project.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.jfamilycounselor.util.csv.CsvUtil;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class ExportReportJob extends Job {

    public static final String FAMILY = "jFamilyCounselorExportReport";

    public static final ISchedulingRule MUTEX = new ISchedulingRule() {
	public boolean contains(ISchedulingRule rule) {
	    return rule == this;
	}

	public boolean isConflicting(ISchedulingRule rule) {
	    return rule == this;
	}
    };

    private static Logger logger = jFCLogger.getJavaLogger();

    private final IJavaProject iJavaProject;
    private final EstimationType estimation;

    public ExportReportJob(EstimationType estimation, IJavaProject iJavaProject) {
	super(estimation + " exporting report...");
	this.iJavaProject = iJavaProject;
	this.estimation = estimation;
    }

    protected IStatus run(IProgressMonitor monitor) {
	var relevantTypesJob = new RelevantTypesJob(iJavaProject);
	relevantTypesJob.setPriority(Job.LONG);
	relevantTypesJob.setSystem(false);
	relevantTypesJob.setUser(true);
	relevantTypesJob.schedule();
	try {
	    relevantTypesJob.join();
	} catch (InterruptedException e) {
	    logger.warning("Thread interrupted: ExportReportJob");
	    return Status.CANCEL_STATUS;
	}

	var relevantTypes = relevantTypesJob.relevantTypes();

	var workload = relevantTypes.size();

	var subMonitor = SubMonitor.convert(monitor, workload);

	var formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
	var timestamp = formatter.format(new Date());

	var wsRoot = iJavaProject.getProject().getWorkspace().getRoot();

	IPath outputFolder;
	try {
	    outputFolder = wsRoot.getFolder(iJavaProject.getOutputLocation()).getLocation();
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Status.error("JavaModelException during finding the output path");
	}

	var reportsFolder = outputFolder.append("jFamilyCounselor-reports");
	reportsFolder.toFile().mkdirs();

	var outputFile = reportsFolder.append(String.format("%s-%s-%s.csv", iJavaProject.getElementName(), estimation.toString(), timestamp.toString())).toFile();

	try {
	    outputFile.createNewFile();
	} catch (IOException e) {
	    logger.warning("IOException: " + e.getMessage());
	    return Status.error("IOException during creating the output file");
	}

	var tableHead = List.of("Class", "Aperture Coverage");

	var start = Instant.now();
	
	CsvUtil.writeRecords(outputFile,

		Stream.concat(
			Stream.of(CsvUtil.convertToCsv(tableHead)),

			relevantTypes.parallelStream()
				.map(t -> {
				    var metaType = Factory.getInstance().createMType(t);
				    Double apertureCoverage = -2.0;

				    if (estimation == EstimationType.NAME_BASED)
					apertureCoverage = metaType.nameApertureCoverage();
				    else 
					apertureCoverage = metaType.assignmentsApertureCoverage();
				    
				    subMonitor.split(1);
				    
				    logger.info(t.getFullyQualifiedName() + ": " + apertureCoverage);

				    return List.of(t.getFullyQualifiedName(), apertureCoverage.toString());
				})
				.map(l -> CsvUtil.convertToCsv(l))

		)

	);
	
	var end = Instant.now();
	
	logger.info(estimation + " export report job for " + iJavaProject.getElementName() + " took: " + Duration.between(start, end).getSeconds() + " seconds");

	return Status.OK_STATUS;
    }

    public boolean belongsTo(Object family) {
	return FAMILY.equals(family);
    }
}
