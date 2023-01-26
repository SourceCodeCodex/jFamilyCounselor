package ro.lrg.jfamilycounselor.type.property;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class NameApertureCoverage extends ApertureCoverage implements IPropertyComputer<Double, MType> {

	public NameApertureCoverage() {
		super(UsedTypesEstimation.NAME_BASED());
	}

}
