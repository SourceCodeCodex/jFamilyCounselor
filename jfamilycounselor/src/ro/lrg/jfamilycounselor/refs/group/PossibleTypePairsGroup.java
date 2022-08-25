package ro.lrg.jfamilycounselor.refs.group;

import jfamilycounselor.metamodel.entity.MConcreteTypePair;
import jfamilycounselor.metamodel.entity.MRefPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class PossibleTypePairsGroup implements IRelationBuilder<MConcreteTypePair, MRefPair> {

	@Override
	public Group<MConcreteTypePair> buildGroup(MRefPair mRefPair) {
		var group = new Group<MConcreteTypePair>();

		for (ro.lrg.jfamilycounselor.core.MConcreteTypePair mConcreteTypePair : mRefPair.getUnderlyingObject()
				.possibleConcreteTypePairs()) {
			group.add(Factory.getInstance().createMConcreteTypePair(mConcreteTypePair));
		}

		return group;
	}

}
