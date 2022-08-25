package ro.lrg.jfamilycounselor.types.property;

import jfamilycounselor.metamodel.entity.MConcreteTypePair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MConcreteTypePair> {

	@Override
	public String compute(MConcreteTypePair mConcreteTypePair) {
		return mConcreteTypePair.getUnderlyingObject().toString();
	}

}
