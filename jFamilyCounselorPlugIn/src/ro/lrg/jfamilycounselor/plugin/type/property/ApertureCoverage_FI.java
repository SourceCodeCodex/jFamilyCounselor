package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselorplugin.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.metamodel.scala.UsedConcreteTypePairsAlgorithm;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage_FI implements IPropertyComputer<Double, MType> {

    @Override
    public Double compute(MType mType) {
	return mType.getUnderlyingObject().apertureCoverage(UsedConcreteTypePairsAlgorithm.assignmentsBasedAlgorithm());
    }

}
