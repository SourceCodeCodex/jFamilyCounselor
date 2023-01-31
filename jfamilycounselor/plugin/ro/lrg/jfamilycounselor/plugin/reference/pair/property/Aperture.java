package ro.lrg.jfamilycounselor.plugin.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class Aperture implements IPropertyComputer<Integer, MReferencesPair> {

    public Integer compute(MReferencesPair mReferencesPair) {
	return mReferencesPair.possibleTypes().getElements().size();
    }

}
