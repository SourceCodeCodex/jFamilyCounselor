package ro.lrg.jfamilycounselor.plugin.references_pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class NameBasedLevenshteinApertureCoverage implements IPropertyComputer<Double, MReferencesPair> {

    @Override
    public Double compute(MReferencesPair mReferencesPair) {
	return (1.0 * mReferencesPair.nameBasedLevenshteinUsedTypes().getElements().size()) / mReferencesPair.aperture();
    }

}
