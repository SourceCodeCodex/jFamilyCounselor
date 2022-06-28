package ro.lrg.jfamilycounselor.plugin.type.group;

import jfamilycounselorplugin.metamodel.entity.MRefPair;
import jfamilycounselorplugin.metamodel.entity.MType;
import jfamilycounselorplugin.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class SusceptibleRefPairsGroup implements IRelationBuilder<MRefPair, MType> {

    @Override
    public Group<MRefPair> buildGroup(MType mType) {
	var group = new Group<MRefPair>();

	for (ro.lrg.jfamilycounselor.MRefPair refPair : mType.getUnderlyingObject().susceptibleRefPairs()) {
	    group.add(Factory.getInstance().createMRefPair(refPair));
	}

	return group;
    }

}
