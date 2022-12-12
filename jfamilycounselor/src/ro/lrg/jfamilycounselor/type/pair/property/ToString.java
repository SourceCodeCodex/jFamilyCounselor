package ro.lrg.jfamilycounselor.type.pair.property;

import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MTypesPair> {

	public String compute(MTypesPair mTypesPair) {
		return mTypesPair.getUnderlyingObject().toString();
	}

}
