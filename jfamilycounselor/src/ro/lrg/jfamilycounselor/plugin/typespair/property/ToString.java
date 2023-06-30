package ro.lrg.jfamilycounselor.plugin.typespair.property;

import jfamilycounselor.metamodel.entity.MTypesPair;
import static ro.lrg.jfamilycounselor.util.stringify.Stringify.stringify;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MTypesPair> {

    @Override
    public String compute(MTypesPair mTypesPair) {
	var t1 = mTypesPair.getUnderlyingObject()._1;
	var t2 = mTypesPair.getUnderlyingObject()._2;
	return "[" + stringify(t1) + ", " + stringify(t2) + "]";
    }

}
