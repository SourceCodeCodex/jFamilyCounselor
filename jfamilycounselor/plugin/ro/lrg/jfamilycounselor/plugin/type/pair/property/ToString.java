package ro.lrg.jfamilycounselor.plugin.type.pair.property;

import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MTypesPair> {

    public String compute(MTypesPair mTypesPair) {
	var t1 = (IType) mTypesPair.getUnderlyingObject()._1;
	var t2 = (IType) mTypesPair.getUnderlyingObject()._2;
	return "[" + t1.getFullyQualifiedName() + ", " + t2.getFullyQualifiedName() + "]";
    }

}
