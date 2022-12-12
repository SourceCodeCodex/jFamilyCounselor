package ro.lrg.jfamilycounselor.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MReferenceVariablesPair> {

	public String compute(MReferenceVariablesPair mReferenceVariablesPair) {
		return mReferenceVariablesPair.getUnderlyingObject().toString();
	}

}
