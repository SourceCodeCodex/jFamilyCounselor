package ro.lrg.jfamilycounselor.plugin.project.action;

import ro.lrg.jfamilycounselor.plugin.impl.UsedConcreteTypePairsAlgorithm;
import ro.lrg.jfamilycounselor.plugin.impl.report.ProjectReportExporter;
import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MProject;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public class ExportReport_FI implements IActionPerformer<Void, MProject, HListEmpty> {

	@Override
	public Void performAction(MProject mProject, HListEmpty arg1) {
		ProjectReportExporter.exportReport(mProject.getUnderlyingObject(),
				UsedConcreteTypePairsAlgorithm.assignmentsBasedAlgorithm());
		return null;
	}

}
