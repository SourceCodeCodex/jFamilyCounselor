package ro.lrg.jfamilycounselor.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.core.UsedConcreteTypePairsAlgorithm;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage_NameBased implements IPropertyComputer<Double, MType> {

	@Override
	public Double compute(MType mType) {
		return mType.getUnderlyingObject().apertureCoverage(UsedConcreteTypePairsAlgorithm.nameBasedAlgorithm());
	}

}
