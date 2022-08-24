package ro.lrg.jfamilycounselor.plugin.refs.property;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class Aperture implements IPropertyComputer<Integer, MRefPair> {

	@Override
	public Integer compute(MRefPair mRefPair) {
		return mRefPair.getUnderlyingObject().aperture();
	}

}
