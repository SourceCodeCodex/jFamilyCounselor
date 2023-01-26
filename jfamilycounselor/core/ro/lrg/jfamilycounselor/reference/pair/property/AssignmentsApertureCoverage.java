package ro.lrg.jfamilycounselor.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class AssignmentsApertureCoverage extends ApertureCoverage implements IPropertyComputer<Double, MReferenceVariablesPair> {

	public AssignmentsApertureCoverage() {
		super(UsedTypesEstimation.ASSIGNMENTS_BASED());
	}

}
