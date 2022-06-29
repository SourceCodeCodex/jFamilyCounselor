package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselorplugin.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage_NS implements IPropertyComputer<Double, MType> {

    @Override
    public Double compute(MType mType) {
	return mType.getUnderlyingObject()
		.apertureCoverage(ro.lrg.jfamilycounselor.alg.UsedConcreteTypePairsAlgorithm.nameBasedAlgorithm());
    }

}
