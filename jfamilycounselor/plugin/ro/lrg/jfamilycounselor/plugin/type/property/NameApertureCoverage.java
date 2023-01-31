package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class NameApertureCoverage implements IPropertyComputer<Double, MType> {

    public Double compute(MType mType) {
	return mType.referencesPairs().getElements().parallelStream().map(p -> p.nameApertureCoverage()).min(Double::compareTo).orElseGet(() -> 0.);
    }

}
