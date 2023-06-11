package ro.lrg.jfamilycounselor.plugin.project.action;

import org.eclipse.core.runtime.jobs.Job;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public class LevenshteinReport implements IActionPerformer<Void, MProject, HListEmpty> {

    public Void performAction(MProject mProject, HListEmpty args) {
	var exportJob = new ExportReportJob(EstimationType.LEVENSHTEIN_BASED, mProject.getUnderlyingObject());
	exportJob.setPriority(Job.LONG);
	exportJob.setRule(ExportReportJob.MUTEX);
	exportJob.setSystem(false);
	exportJob.setUser(true);
	exportJob.schedule();

	return null;
    }

}
