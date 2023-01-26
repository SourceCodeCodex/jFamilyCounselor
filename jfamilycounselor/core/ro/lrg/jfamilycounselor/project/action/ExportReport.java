package ro.lrg.jfamilycounselor.project.action;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.jfamilycounselor.core.report.*;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

public abstract class ExportReport implements IActionPerformer<Void, MProject, HListEmpty> {

	private UsedTypesEstimation estimation;

	protected ExportReport(UsedTypesEstimation estimation) {
		this.estimation = estimation;
	}

	public Void performAction(MProject mProject, HListEmpty arg1) {
		ReportExporter.exportReport(mProject.getUnderlyingObject(), estimation);
		return null;
	}

}
