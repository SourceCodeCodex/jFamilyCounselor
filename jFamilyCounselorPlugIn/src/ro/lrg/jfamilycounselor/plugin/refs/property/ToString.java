package ro.lrg.jfamilycounselor.plugin.refs.property;

import jfamilycounselorplugin.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MRefPair> {

    @Override
    public String compute(MRefPair mRefPair) {
	return mRefPair.getUnderlyingObject().toString();
    }

}
