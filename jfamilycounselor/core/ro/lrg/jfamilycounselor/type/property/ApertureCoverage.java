package ro.lrg.jfamilycounselor.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.IPropertyComputer;

public abstract class ApertureCoverage implements IPropertyComputer<Double, MType> {

	private UsedTypesEstimation estimation;
	
	protected ApertureCoverage(UsedTypesEstimation estimation) {
		this.estimation = estimation;
	}

	
	@Override
	public Double compute(MType mType) {
		return mType.getUnderlyingObject().apertureCoverage(estimation);
	}

	
}
