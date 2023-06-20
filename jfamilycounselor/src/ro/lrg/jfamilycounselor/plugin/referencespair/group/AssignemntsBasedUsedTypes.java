package ro.lrg.jfamilycounselor.plugin.referencespair.group;

import static ro.lrg.jfamilycounselor.approach.usedtypes.assignment.AssignmentsBasedApproach.usedTypes;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class AssignemntsBasedUsedTypes implements IRelationBuilder<MTypesPair, MReferencesPair> {

    @Override
    public Group<MTypesPair> buildGroup(MReferencesPair mReferencesPair) {
	var group = new Group<MTypesPair>();
	Optional<List<Pair<IType, IType>>> usedTypes = usedTypes(mReferencesPair.getUnderlyingObject());

	if (usedTypes.isEmpty())
	    throw new IllegalStateException("Assignments-based used types computation for pair: " + mReferencesPair.toString() + " failed.");

	// if assignments based cannot find any used types, then consider all possible types
	if (usedTypes.get().isEmpty())
	    usedTypes = Optional.of(mReferencesPair.possibleTypes().getElements().stream().map(mTypesPair -> (Pair<IType, IType>) mTypesPair.getUnderlyingObject()).toList());

	usedTypes.ifPresent(l -> l.stream()
		.map(p -> Factory.getInstance().createMTypesPair(p))
		.forEach(p -> group.add(p)));

	return group;
    }

}
