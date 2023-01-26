package ro.lrg.jfamilycounselor.reference.pair.group;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class NameUsedTypes extends UsedTypes implements IRelationBuilder<MTypesPair, MReferenceVariablesPair> {
	
	public NameUsedTypes() {
		super(UsedTypesEstimation.NAME_BASED());
	}

}
