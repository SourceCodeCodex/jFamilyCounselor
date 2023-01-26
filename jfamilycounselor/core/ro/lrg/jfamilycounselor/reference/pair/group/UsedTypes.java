package ro.lrg.jfamilycounselor.reference.pair.group;

import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;

public abstract class UsedTypes implements IRelationBuilder<MTypesPair, MReferenceVariablesPair> {

	private UsedTypesEstimation estimation;

	protected UsedTypes(UsedTypesEstimation estimation) {
		this.estimation = estimation;
	}

	public Group<MTypesPair> buildGroup(MReferenceVariablesPair mReferenceVariablesPair) {
		var group = new Group<MTypesPair>();
		mReferenceVariablesPair.getUnderlyingObject()
				.usedTypes(estimation)
				.map(Factory.getInstance()::createMTypesPair)
				.foreach(tp -> group.add(tp));
		return group;
	}

}
