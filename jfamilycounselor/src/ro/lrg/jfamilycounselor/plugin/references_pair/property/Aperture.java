package ro.lrg.jfamilycounselor.plugin.references_pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class Aperture implements IPropertyComputer<Integer, MReferencesPair> {

    @Override
    public Integer compute(MReferencesPair mReferencesPair) {
	return mReferencesPair.possibleTypes().getElements().size();
    }

}
