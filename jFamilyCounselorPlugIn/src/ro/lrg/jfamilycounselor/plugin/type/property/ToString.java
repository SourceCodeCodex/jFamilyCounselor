package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselorplugin.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MType> {

    @Override
    public String compute(MType mType) {
	return mType.getUnderlyingObject().toString();
    }

}
