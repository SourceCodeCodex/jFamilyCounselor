package ro.lrg.jfamilycounselor.type.group;

import jfamilycounselor.metamodel.entity.MRefPair;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class MightHideFamilialCorrelationsRefPairs implements IRelationBuilder<MRefPair, MType> {

	@Override
	public Group<MRefPair> buildGroup(MType mType) {
		var group = new Group<MRefPair>();

		for (ro.lrg.jfamilycounselor.core.MRefPair refPair : mType.getUnderlyingObject().mightHideFamilialCorrelationsRefPairs()) {
			group.add(Factory.getInstance().createMRefPair(refPair));
		}

		return group;
	}

}
