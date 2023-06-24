package ro.lrg.jfamilycounselor.plugin.referencespair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class AssignmentsBasedApertureCoverage implements IPropertyComputer<Double, MReferencesPair> {
    public Double compute(MReferencesPair mReferencesPair) {
    	Group<MTypesPair> usedConcreteTypePairs 
			= mReferencesPair.assignmentsBasedUsedTypes();
    	double cardinalityUsedConcreteTypePairs 
			= usedConcreteTypePairs.getElements().size();
    	double cardinalityPossibleConcreteTypePairs 
			= mReferencesPair.aperture();
    	return cardinalityUsedConcreteTypePairs 
    			/ cardinalityPossibleConcreteTypePairs;
    }
}
