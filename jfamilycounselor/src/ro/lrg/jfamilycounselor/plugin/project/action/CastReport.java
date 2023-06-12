package ro.lrg.jfamilycounselor.plugin.project.action;

import org.eclipse.core.runtime.jobs.Job;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.Constants.EstimationType;
import ro.lrg.jfamilycounselor.report.ExportReportJob;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public class CastReport implements IActionPerformer<Void, MProject, HListEmpty> {

    @Override
    public Void performAction(MProject mProject, HListEmpty args) {
	var exportJob = new ExportReportJob(EstimationType.CAST_BASED, mProject.getUnderlyingObject());
	exportJob.setPriority(Job.LONG);
	exportJob.setRule(ExportReportJob.MUTEX);
	exportJob.setSystem(false);
	exportJob.setUser(true);
	exportJob.schedule();

	return null;
    }

}
