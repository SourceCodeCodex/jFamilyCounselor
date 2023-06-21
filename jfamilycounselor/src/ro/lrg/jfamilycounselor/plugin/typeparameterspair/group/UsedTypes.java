package ro.lrg.jfamilycounselor.plugin.typeparameterspair.group;

import static ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.TypeParametersUsedTypesApproach.usedTypes;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class UsedTypes implements IRelationBuilder<MTypesPair, MTypeParametersPair> {

    @Override
    public Group<MTypesPair> buildGroup(MTypeParametersPair mTypeParametersPair) {
	var group = new Group<MTypesPair>();

	Optional<List<Pair<IType, IType>>> usedTypes = usedTypes(mTypeParametersPair.getUnderlyingObject());

	if (usedTypes.isEmpty())
	    throw new IllegalStateException("Type-parameters-based used types computation for pair: " + mTypeParametersPair.toString() + " failed. The result should be a non-empty list.");

	// if type parameters based cannot find any used types, then consider all possible types
	if (usedTypes.get().isEmpty())
	    usedTypes = Optional.of(mTypeParametersPair.possibleTypes().getElements().stream().map(mTypesPair -> (Pair<IType, IType>) mTypesPair.getUnderlyingObject()).toList());

	usedTypes.get().stream()
		.map(p -> Factory.getInstance().createMTypesPair(p))
		.forEach(p -> group.add(p));

	return group;
    }

}
