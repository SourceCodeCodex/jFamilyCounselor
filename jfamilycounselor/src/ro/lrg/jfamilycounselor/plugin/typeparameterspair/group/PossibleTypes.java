package ro.lrg.jfamilycounselor.plugin.typeparameterspair.group;

import static ro.lrg.jfamilycounselor.capability.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;
import static ro.lrg.jfamilycounselor.capability.typeparameter.TypeParameterCapability.typeParameterBoundsTypes;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;

import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import jfamilycounselor.metamodel.entity.MTypesPair;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class PossibleTypes implements IRelationBuilder<MTypesPair, MTypeParametersPair> {

	@Override
	public Group<MTypesPair> buildGroup(MTypeParametersPair mTypeParametersPair) {
		var group = new Group<MTypesPair>();
		var pair = mTypeParametersPair.getUnderlyingObject();
		Optional<List<Pair<IType, IType>>> possibleTypes = Optional.empty();

		if (pair._1 instanceof IType thiz && pair._2 instanceof ITypeParameter typeParam) {
			var bounds = typeParameterBoundsTypes(typeParam);
			if (bounds.isEmpty() || bounds.get().size() != 1)
				throw new IllegalStateException("The number of type bounds for: " + typeParam
						+ " is different than 1. Previous checks need to ensure that this situation cannot happen.");

			possibleTypes = distinctConcreteConeProduct(thiz, bounds.get().get(0));
		} else if (pair._1 instanceof ITypeParameter typeParam1 && pair._2 instanceof ITypeParameter typeParam2) {
			var bounds1 = typeParameterBoundsTypes(typeParam1);
			if (bounds1.isEmpty() || bounds1.get().size() != 1)
				throw new IllegalStateException("The number of type bounds for: " + typeParam1
						+ " is different than 1. Previous checks need to ensure that this situation cannot happen.");

			var bounds2 = typeParameterBoundsTypes(typeParam2);
			if (bounds2.isEmpty() || bounds2.get().size() != 1)
				throw new IllegalStateException("The number of type bounds for: " + typeParam2
						+ " is different than 1. Previous checks need to ensure that this situation cannot happen.");

			possibleTypes = distinctConcreteConeProduct(bounds1.get().get(0), bounds2.get().get(0));
		}
		if (possibleTypes.isEmpty() || possibleTypes.get().isEmpty())
			throw new IllegalStateException("Possible types computation for pair: " + mTypeParametersPair.toString()
					+ " failed or resulted in an empty list. Previous checks need to ensure that this situation cannot happen.");

		possibleTypes.get().stream().map(p -> Factory.getInstance().createMTypesPair(p)).forEach(p -> group.add(p));

		return group;
	}

}
