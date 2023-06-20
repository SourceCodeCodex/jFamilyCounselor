package ro.lrg.jfamilycounselor.plugin.typeparameterspair.property;

import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class Aperture implements IPropertyComputer<Integer, MTypeParametersPair> {

    @Override
    public Integer compute(MTypeParametersPair mTypeParametersPair) {
	return mTypeParametersPair.possibleTypes().getElements().size();
    }

}
