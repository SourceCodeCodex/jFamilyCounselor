package ro.lrg.jfamilycounselor.refs.property;

import jfamilycounselor.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MRefPair> {

	@Override
	public String compute(MRefPair mRefPair) {
		return mRefPair.getUnderlyingObject().toString();
	}

}
