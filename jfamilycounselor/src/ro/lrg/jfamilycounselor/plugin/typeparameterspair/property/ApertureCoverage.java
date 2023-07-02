package ro.lrg.jfamilycounselor.plugin.typeparameterspair.property;

import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ApertureCoverage implements IPropertyComputer<Double, MTypeParametersPair> {
	public Double compute(MTypeParametersPair mTypeParametersPair) {
		Group<MTypesPair> usedConcreteTypePairs = mTypeParametersPair.usedTypes();
		double cardinalityUsedConcreteTypePairs = usedConcreteTypePairs.getElements().size();
		double cardinalityPossibleConcreteTypePairs = mTypeParametersPair.aperture();
		return cardinalityUsedConcreteTypePairs / cardinalityPossibleConcreteTypePairs;
	}
}