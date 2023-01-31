package ro.lrg.jfamilycounselor.plugin.reference.pair.group;

import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.capability.specific.coverage.name.UsedTypesCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class NameUsedTypes implements IRelationBuilder<MTypesPair, MReferencesPair> {

    @SuppressWarnings("unchecked")
    public Group<MTypesPair> buildGroup(MReferencesPair mReferencesPair) {
	var group = new Group<MTypesPair>();
	group.addAll(
		UsedTypesCapability.usedTypes(
			mReferencesPair
				.possibleTypes()
				.getElements()
				.stream()
				.map(p -> (Pair<IType, IType>) p.getUnderlyingObject())
				.toList()

		)
			.stream()
			.map(p -> Factory.getInstance().createMTypesPair(p))
			.toList()
	);
	return group;
    }

}
