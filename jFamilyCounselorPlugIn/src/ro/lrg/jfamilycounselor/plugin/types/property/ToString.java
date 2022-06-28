package ro.lrg.jfamilycounselor.plugin.types.property;

import jfamilycounselorplugin.metamodel.entity.MConcreteTypePair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MConcreteTypePair> {

    @Override
    public String compute(MConcreteTypePair mConcreteTypePair) {
	return mConcreteTypePair.getUnderlyingObject().toString();
    }

}
