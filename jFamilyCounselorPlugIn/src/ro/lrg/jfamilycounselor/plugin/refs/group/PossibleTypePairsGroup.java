package ro.lrg.jfamilycounselor.plugin.refs.group;

import jfamilycounselorplugin.metamodel.entity.MConcreteTypePair;
import jfamilycounselorplugin.metamodel.entity.MRefPair;
import jfamilycounselorplugin.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class PossibleTypePairsGroup implements IRelationBuilder<MConcreteTypePair, MRefPair> {

    @Override
    public Group<MConcreteTypePair> buildGroup(MRefPair mRefPair) {
	var group = new Group<MConcreteTypePair>();

	for (ro.lrg.jfamilycounselor.metamodel.scala.MConcreteTypePair mConcreteTypePair : mRefPair
		.getUnderlyingObject()
		.possibleConcreteTypePairs()) {
	    group.add(Factory.getInstance().createMConcreteTypePair(mConcreteTypePair));
	}

	return group;
    }

}
