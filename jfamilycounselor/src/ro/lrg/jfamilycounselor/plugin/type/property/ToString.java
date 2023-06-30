package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.util.stringify.Stringify;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MType> {

    @Override
    public String compute(MType mType) {
	return Stringify.stringify(mType.getUnderlyingObject());
    }

}
