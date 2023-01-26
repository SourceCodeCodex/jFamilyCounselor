package ro.lrg.jfamilycounselor.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class Aperture implements IPropertyComputer<Integer, MReferenceVariablesPair> {

	public Integer compute(MReferenceVariablesPair mReferenceVariablesPair) {
		return mReferenceVariablesPair.getUnderlyingObject().aperture();
	}

}
