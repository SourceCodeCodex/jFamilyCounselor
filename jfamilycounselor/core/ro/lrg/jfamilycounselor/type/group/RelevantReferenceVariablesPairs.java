package ro.lrg.jfamilycounselor.type.group;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class RelevantReferenceVariablesPairs implements IRelationBuilder<MReferenceVariablesPair, MType> {

	@Override
	public Group<MReferenceVariablesPair> buildGroup(MType mType) {
		var group = new Group<MReferenceVariablesPair>();
		mType.getUnderlyingObject()
			.relevantReferenceVariablesPairs()
			.map(Factory.getInstance()::createMReferenceVariablesPair)
			.foreach(rp -> group.add(rp));
		return group;
	}

}
