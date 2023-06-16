package ro.lrg.jfamilycounselor.plugin.references_pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class CastsBasedApertureCoverage implements IPropertyComputer<Double, MReferencesPair> {

    @Override
    public Double compute(MReferencesPair mReferencesPair) {
	return (1.0 * mReferencesPair.castsBasedUsedTypes().getElements().size()) / mReferencesPair.aperture();
    }

}
