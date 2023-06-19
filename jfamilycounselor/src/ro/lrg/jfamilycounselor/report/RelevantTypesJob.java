package ro.lrg.jfamilycounselor.report;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.approach.relevance.reference.RelevantTypesByReferencesUtil;
import ro.lrg.jfamilycounselor.capability.project.AllTypesCapability;
import ro.lrg.jfamilycounselor.util.duration.DurationFormatter;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class RelevantTypesJob extends Job {

    private final IJavaProject iJavaProject;
    
    private static Logger logger = jFCLogger.getLogger();

    public RelevantTypesJob(IJavaProject iJavaProject) {
	super("Computing relevant types of " + iJavaProject.getElementName() + "...");
	this.iJavaProject = iJavaProject;
    }

    private final ConcurrentLinkedQueue<IType> relevantTypes = new ConcurrentLinkedQueue<>();

    protected IStatus run(IProgressMonitor monitor) {
	var start = Instant.now(); 
	
	var types = AllTypesCapability.allTypes(iJavaProject);

	var workload = types.size();

	var subMonitor = SubMonitor.convert(monitor, workload);

	types.parallelStream().forEach(t -> {
	    subMonitor.split(1);
	    if (RelevantTypesByReferencesUtil.isRelevant(t))
		relevantTypes.add(t);
	});
	
	var end = Instant.now();
	
	logger.info("Relevant types job for " + iJavaProject.getElementName() + " took: " + DurationFormatter.format(Duration.between(start, end)) + ". Number of relevant types: " + relevantTypes.size());

	return Status.OK_STATUS;

    }

    public List<IType> relevantTypes() {
	return new ArrayList<>(relevantTypes);
    }

    public boolean belongsTo(Object family) {
	return ExportReportJob.FAMILY.equals(family);
    }

}
