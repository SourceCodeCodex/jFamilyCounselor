package ro.lrg.jfamilycounselor.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.IPropertyComputer;

public abstract class ApertureCoverage implements IPropertyComputer<Double, MReferenceVariablesPair> {
	
	private UsedTypesEstimation estimation;
	
	protected ApertureCoverage(UsedTypesEstimation estimation) {
		this.estimation = estimation;
	}
	
	public Double compute(MReferenceVariablesPair mReferenceVariablesPair) {
		return mReferenceVariablesPair.getUnderlyingObject().apertureCoverage(estimation);
	}

}
