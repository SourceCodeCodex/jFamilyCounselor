package ro.lrg.jfamilycounselor.plugin.type.group;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MRefPair;
import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.plugin.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class SusceptibleRefPairsGroup implements IRelationBuilder<MRefPair, MType> {

	@Override
	public Group<MRefPair> buildGroup(MType mType) {
		var group = new Group<MRefPair>();

		for (ro.lrg.jfamilycounselor.plugin.impl.MRefPair refPair : mType.getUnderlyingObject().susceptibleRefPairs()) {
			group.add(Factory.getInstance().createMRefPair(refPair));
		}

		return group;
	}

}
