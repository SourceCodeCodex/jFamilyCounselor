package ro.lrg.jfamilycounselor.project.action;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.core.UsedConcreteTypePairsAlgorithm;
import ro.lrg.jfamilycounselor.core.report.ProjectReportExporter;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public class ExportReport_NameBased implements IActionPerformer<Void, MProject, HListEmpty> {

	@Override
	public Void performAction(MProject mProject, HListEmpty arg1) {
		ProjectReportExporter.exportReport(mProject.getUnderlyingObject(),
				UsedConcreteTypePairsAlgorithm.nameBasedAlgorithm());
		return null;
	}

}