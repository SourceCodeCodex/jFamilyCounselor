package ro.lrg.jfamilycounselor.plugin.refs.property;

import jfamilycounselorplugin.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage_FI implements IPropertyComputer<Double, MRefPair> {

    @Override
    public Double compute(MRefPair mRefPair) {
	return mRefPair.getUnderlyingObject()
		.apertureCoverage(ro.lrg.jfamilycounselor.alg.UsedConcreteTypePairsAlgorithm.nameBasedAlgorithm());
    }

}
