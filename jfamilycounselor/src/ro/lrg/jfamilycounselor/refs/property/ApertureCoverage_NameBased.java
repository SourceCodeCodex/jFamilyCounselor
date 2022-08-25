package ro.lrg.jfamilycounselor.refs.property;

import jfamilycounselor.metamodel.entity.MRefPair;
import ro.lrg.jfamilycounselor.core.UsedConcreteTypePairsAlgorithm;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage_NameBased implements IPropertyComputer<Double, MRefPair> {

	@Override
	public Double compute(MRefPair mRefPair) {
		return mRefPair.getUnderlyingObject().apertureCoverage(UsedConcreteTypePairsAlgorithm.nameBasedAlgorithm());
	}

}
