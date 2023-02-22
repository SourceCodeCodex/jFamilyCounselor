package ro.lrg.jfamilycounselor.plugin.reference.pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class AssignmentApertureCoverage implements IPropertyComputer<Double, MReferencesPair> {

    public Double compute(MReferencesPair mReferencesPair) {
	return (1.0 * mReferencesPair.assignemntUsedTypes().getElements().size()) / mReferencesPair.aperture();
    }

}
