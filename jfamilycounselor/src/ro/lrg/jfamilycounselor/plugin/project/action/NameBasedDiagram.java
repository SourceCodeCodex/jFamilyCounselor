package ro.lrg.jfamilycounselor.plugin.project.action;

import org.eclipse.core.runtime.jobs.Job;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.diagram.ExportDiagramRelationsJob;
import ro.lrg.jfamilycounselor.util.Constants.EstimationType;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenDiagramInBrowser implements IActionPerformer<Void, MProject, HListEmpty> {

	@Override
	public Void performAction(MProject mProject, HListEmpty args) {
		var exportJob = new ExportDiagramRelationsJob(EstimationType.NAME_BASED, mProject.getUnderlyingObject());
		exportJob.setPriority(Job.LONG);
		exportJob.setRule(ExportDiagramRelationsJob.MUTEX);
		exportJob.setSystem(false);
		exportJob.setUser(true);
		exportJob.schedule();

		return null;
	}

}
