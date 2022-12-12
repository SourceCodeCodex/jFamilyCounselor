package ro.lrg.jfamilycounselor.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MType> {

	public String compute(MType mType) {
		return mType.getUnderlyingObject().toString();
	}

}
