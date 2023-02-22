package ro.lrg.jfamilycounselor.plugin.reference.pair.group;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.capability.generic.type.ParameterTypeCapability;
import ro.lrg.jfamilycounselor.capability.specific.coverage.assignment.AssignmentUsedTypesCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class AssignemntUsedTypes implements IRelationBuilder<MTypesPair, MReferencesPair> {

    @SuppressWarnings("unchecked")
    public Group<MTypesPair> buildGroup(MReferencesPair mReferencesPair) {
	var group = new Group<MTypesPair>();
	var pair = mReferencesPair.getUnderlyingObject();

	Optional<List<Pair<IType, IType>>> usedTypes = Optional.empty();
	if (pair._1 instanceof IType t1 && pair._2 instanceof ILocalVariable param2) {
	    var t2 = ParameterTypeCapability.parameterType(param2);
	    if (t2.isEmpty())
		throw new IllegalStateException("Name used types computation for pair: " + mReferencesPair.toString() + " failed. Previous checks need to ensure that this situation cannot happen.");

	    usedTypes = AssignmentUsedTypesCapability.usedTypes((Pair<IJavaElement, IJavaElement>) pair, Pair.of(t1, t2.get()));
	} else if (pair._1 instanceof ILocalVariable param1 && pair._2 instanceof ILocalVariable param2) {
	    var t1 = ParameterTypeCapability.parameterType(param1);
	    var t2 = ParameterTypeCapability.parameterType(param2);

	    if (t1.isEmpty() || t2.isEmpty())
		throw new IllegalStateException("Name used types computation for pair: " + mReferencesPair.toString() + " failed. Previous checks need to ensure that this situation cannot happen.");

	    usedTypes = AssignmentUsedTypesCapability.usedTypes((Pair<IJavaElement, IJavaElement>) pair, Pair.of(t1.get(), t2.get()));
	}

	usedTypes.ifPresent(l -> l.stream()
		.map(p -> Factory.getInstance().createMTypesPair(p))
		.forEach(p -> group.add(p)));

	return group;
    }

}
