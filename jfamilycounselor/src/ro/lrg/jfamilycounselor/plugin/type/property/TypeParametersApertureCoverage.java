package ro.lrg.jfamilycounselor.plugin.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class TypeParametersApertureCoverage implements IPropertyComputer<Double, MType> {

	@Override
	public Double compute(MType mType) {
		return mType.relevantTypeParametersPairs().getElements().parallelStream().map(p -> p.apertureCoverage())
				.toList().stream().min(Double::compareTo).orElse(0.);
	}

}
