package ro.lrg.jfamilycounselor.plugin.refs.group;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MConcreteTypePair;
import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MRefPair;
import ro.lrg.jfamilycounselor.plugin.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.plugin.impl.UsedConcreteTypePairsAlgorithm;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class UsedTypePairsGroup_FI implements IRelationBuilder<MConcreteTypePair, MRefPair> {

	@Override
	public Group<MConcreteTypePair> buildGroup(MRefPair mRefPair) {
		var group = new Group<MConcreteTypePair>();

		for (ro.lrg.jfamilycounselor.plugin.impl.MConcreteTypePair mConcreteTypePair : mRefPair.getUnderlyingObject()
				.usedConcreteTypePairs(UsedConcreteTypePairsAlgorithm.assignmentsBasedAlgorithm())) {
			group.add(Factory.getInstance().createMConcreteTypePair(mConcreteTypePair));
		}

		return group;
	}

}
