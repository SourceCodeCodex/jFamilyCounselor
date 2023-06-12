package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class NameApertureCoverage implements IPropertyComputer<Double, MType> {

    @Override
    public Double compute(MType mType) {
	return mType.referencesPairs().getElements().parallelStream()
		.map(p -> p.nameApertureCoverage())
		.toList().stream()
		.filter(d -> d != 0)
		.min(Double::compareTo)
		.orElse(0.);
    }

}
