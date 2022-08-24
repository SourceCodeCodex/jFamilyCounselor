package ro.lrg.jfamilycounselor.plugin.refs.property;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MRefPair;
import ro.lrg.jfamilycounselor.plugin.impl.UsedConcreteTypePairsAlgorithm;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage_NS implements IPropertyComputer<Double, MRefPair> {

	@Override
	public Double compute(MRefPair mRefPair) {
		return mRefPair.getUnderlyingObject().apertureCoverage(UsedConcreteTypePairsAlgorithm.nameBasedAlgorithm());
	}

}
