package ro.lrg.jfamilycounselor.plugin.type.group;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.approach.reference.relevance.RelevantReferencesPairsUtil;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class RelevantReferencesPairs implements IRelationBuilder<MReferencesPair, MType> {

    @Override
    public Group<MReferencesPair> buildGroup(MType mType) {
	var group = new Group<MReferencesPair>();
	RelevantReferencesPairsUtil.relevantReferencesPairs(mType.getUnderlyingObject()).stream()
		.map(p -> Factory.getInstance().createMReferencesPair(p))
		.forEach(p -> group.add(p));

	return group;
    }

}
