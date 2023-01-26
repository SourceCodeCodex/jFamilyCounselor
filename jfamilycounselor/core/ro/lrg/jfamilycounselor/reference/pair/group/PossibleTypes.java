package ro.lrg.jfamilycounselor.reference.pair.group;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class PossibleTypes implements IRelationBuilder<MTypesPair, MReferenceVariablesPair> {

	public Group<MTypesPair> buildGroup(MReferenceVariablesPair mReferenceVariablesPair) {
		var group = new Group<MTypesPair>();
		mReferenceVariablesPair.getUnderlyingObject()
				.possibleTypes()
				.map(Factory.getInstance()::createMTypesPair)
				.foreach(tp -> group.add(tp));
		return group;
	}

}
