package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class NameBasedApertureCoverage implements IPropertyComputer<Double, MType> {

    @Override
    public Double compute(MType mType) {
	return mType.referencesPairs().getElements().parallelStream()
		.map(p -> p.nameBasedApertureCoverage())
		.toList().stream()
		.min(Double::compareTo)
		.orElse(1.);
    }

}
