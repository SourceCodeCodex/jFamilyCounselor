package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class CastApertureCoverage implements IPropertyComputer<Double, MType> {

    public Double compute(MType mType) {
	return mType.referencesPairs().getElements().parallelStream()
		.map(p -> p.castApertureCoverage())
		.toList().stream()
		.filter(d -> d != 0)
		.min(Double::compareTo)
		.orElse(0.);
    }

}