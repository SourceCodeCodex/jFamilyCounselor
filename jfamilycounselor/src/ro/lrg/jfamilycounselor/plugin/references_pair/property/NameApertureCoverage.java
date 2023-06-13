package ro.lrg.jfamilycounselor.plugin.references_pair.property;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class NameApertureCoverage 
	implements IPropertyComputer<Double, MReferencesPair> {

    public Double compute(MReferencesPair mReferencesPair) {
    	Group<MTypesPair> usedConcreteTypePairs 
    		= mReferencesPair.nameUsedTypes();
    	double cardinalityUsedConcreteTypePairs 
    		= usedConcreteTypePairs.getElements().size();
    	double cardinalityPossibleConcreteTypePairs 
    		= mReferencesPair.aperture();
    	return cardinalityUsedConcreteTypePairs 
    			/ cardinalityPossibleConcreteTypePairs;
    }
}