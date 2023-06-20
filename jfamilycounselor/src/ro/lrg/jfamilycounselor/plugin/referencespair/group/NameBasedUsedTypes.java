package ro.lrg.jfamilycounselor.plugin.referencespair.group;

import static ro.lrg.jfamilycounselor.capability.parameter.ParameterTypeCapability.parameterType;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.approach.reference.usedtypes.name.NameBasedApproach;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class NameBasedUsedTypes implements IRelationBuilder<MTypesPair, MReferencesPair> {

    @Override
    public Group<MTypesPair> buildGroup(MReferencesPair mReferencesPair) {
	var group = new Group<MTypesPair>();
	var pair = mReferencesPair.getUnderlyingObject();
	Optional<List<Pair<IType, IType>>> usedTypes = Optional.empty();
	if (pair._1 instanceof IType thiz && pair._2 instanceof ILocalVariable param)
	    usedTypes = parameterType(param).flatMap(paramType -> NameBasedApproach.instance().usedTypes(thiz, paramType));
	else if (pair._1 instanceof ILocalVariable param1 && pair._2 instanceof ILocalVariable param2)
	    usedTypes = parameterType(param1).flatMap(t1 -> parameterType(param2).flatMap(t2 -> NameBasedApproach.instance().usedTypes(t1, t2)));

	if (usedTypes.isEmpty() || usedTypes.get().isEmpty())
	    throw new IllegalStateException("Name-based used types computation for pair: " + mReferencesPair.toString() + " failed. The result should be a non-empty list.");

	usedTypes.get().stream()
		.map(p -> Factory.getInstance().createMTypesPair(p))
		.forEach(p -> group.add(p));

	return group;
    }

}
