package ro.lrg.jfamilycounselor.project.action;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public class NameReport extends ExportReport implements IActionPerformer<Void, MProject, HListEmpty> {

	public NameReport() {
		super(UsedTypesEstimation.NAME_BASED());
	}

}