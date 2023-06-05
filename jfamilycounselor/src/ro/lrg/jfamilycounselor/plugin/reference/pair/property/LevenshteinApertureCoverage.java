package ro.lrg.jfamilycounselor.plugin.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class LevenshteinApertureCoverage implements IPropertyComputer<Double, MReferencesPair> {

    public Double compute(MReferencesPair mReferencesPair) {
	return (1.0 * mReferencesPair.levenshteinUsedTypes().getElements().size()) / mReferencesPair.aperture();
    }

}
