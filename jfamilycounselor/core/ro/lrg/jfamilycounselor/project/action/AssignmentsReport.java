package ro.lrg.jfamilycounselor.project.action;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public class AssignmentsReport extends ExportReport implements IActionPerformer<Void, MProject, HListEmpty> {

	public AssignmentsReport() {
		super(UsedTypesEstimation.ASSIGNMENTS_BASED());
	}

}
