package ro.lrg.jfamilycounselor.plugin.references_pair.group;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.capability.coverage.name.NameUsedTypesCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class NameUsedTypes implements IRelationBuilder<MTypesPair, MReferencesPair> {

    @SuppressWarnings("unchecked")
    public Group<MTypesPair> buildGroup(MReferencesPair mReferencesPair) {
	var group = new Group<MTypesPair>();
	var pair = mReferencesPair.getUnderlyingObject();
	Optional<List<Pair<IType, IType>>> usedTypes = Optional.empty();
	if (pair._1 instanceof IType && pair._2 instanceof ILocalVariable)
	    usedTypes = NameUsedTypesCapability.usedTypesThisParam((Pair<IType, ILocalVariable>) pair);
	else if (pair._1 instanceof ILocalVariable && pair._2 instanceof ILocalVariable)
	    usedTypes = NameUsedTypesCapability.usedTypesParamParam((Pair<ILocalVariable, ILocalVariable>) pair);

	if (usedTypes.isEmpty())
	    throw new IllegalStateException("Name used types computation for pair: " + mReferencesPair.toString() + " failed. Previous checks need to ensure that this situation cannot happen.");

	usedTypes.get().stream()
		.map(p -> Factory.getInstance().createMTypesPair(p))
		.forEach(p -> group.add(p));

	return group;
    }

}
