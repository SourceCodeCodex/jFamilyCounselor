package ro.lrg.jfamilycounselor.refs.property;

import jfamilycounselor.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class Aperture implements IPropertyComputer<Integer, MRefPair> {

	@Override
	public Integer compute(MRefPair mRefPair) {
		return mRefPair.getUnderlyingObject().aperture();
	}

}
