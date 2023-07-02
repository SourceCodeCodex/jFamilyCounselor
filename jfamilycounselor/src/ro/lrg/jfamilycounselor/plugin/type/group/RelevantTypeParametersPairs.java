package ro.lrg.jfamilycounselor.plugin.type.group;

import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.distrinctCombinations2;

import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.approach.typeparameter.relevance.RelevantTypeParametersUtil;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class RelevantTypeParametersPairs implements IRelationBuilder<MTypeParametersPair, MType> {

	@Override
	public Group<MTypeParametersPair> buildGroup(MType mType) {
		var group = new Group<MTypeParametersPair>();
		distrinctCombinations2(RelevantTypeParametersUtil.relevantTypeParameters(mType.getUnderlyingObject()))
				.forEach(p -> group.add(Factory.getInstance().createMTypeParametersPair(p)));

		return group;
	}

}
