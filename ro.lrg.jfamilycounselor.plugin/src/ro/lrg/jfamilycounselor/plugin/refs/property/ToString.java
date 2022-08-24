package ro.lrg.jfamilycounselor.plugin.refs.property;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MRefPair> {

	@Override
	public String compute(MRefPair mRefPair) {
		return mRefPair.getUnderlyingObject().toString();
	}

}
