package ro.lrg.jfamilycounselor.plugin.referencespair.group;

import static ro.lrg.jfamilycounselor.capability.parameter.ParameterTypeCapability.parameterType;
import static ro.lrg.jfamilycounselor.capability.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class PossibleTypes implements IRelationBuilder<MTypesPair, MReferencesPair> {

	@Override
	public Group<MTypesPair> buildGroup(MReferencesPair mReferencesPair) {
		var group = new Group<MTypesPair>();
		var pair = mReferencesPair.getUnderlyingObject();
		Optional<List<Pair<IType, IType>>> possibleTypes = Optional.empty();
		if (pair._1 instanceof IType thiz && pair._2 instanceof ILocalVariable param)
			possibleTypes = parameterType(param).flatMap(paramType -> distinctConcreteConeProduct(thiz, paramType));
		else if (pair._1 instanceof ILocalVariable param1 && pair._2 instanceof ILocalVariable param2)
			possibleTypes = parameterType(param1)
					.flatMap(t1 -> parameterType(param2).flatMap(t2 -> distinctConcreteConeProduct(t1, t2)));

		if (possibleTypes.isEmpty() || possibleTypes.get().isEmpty())
			throw new IllegalStateException("Possible types computation for pair: " + mReferencesPair.toString()
					+ " failed or resulted in an empty list. Previous checks need to ensure that this situation cannot happen.");

		possibleTypes.get().stream().map(p -> Factory.getInstance().createMTypesPair(p)).forEach(p -> group.add(p));

		return group;
	}

}
