package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes;

import static ro.lrg.jfamilycounselor.capability.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;

import ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.handler.ThisTypeParameterHandler;
import ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.handler.TypeParameterTypeParameterHandler;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class TypeParametersUsedTypesApproach {
	private TypeParametersUsedTypesApproach() {
	}

	public static Optional<List<Pair<IType, IType>>> usedTypes(Pair<IJavaElement, IJavaElement> typeParametersPair) {
		var state = State.empty();

		if (typeParametersPair._1 instanceof IType thiz
				&& typeParametersPair._2 instanceof ITypeParameter typeParameter) {
			var handledSuccessfully = ThisTypeParameterHandler.handle(thiz, typeParameter, state);
			if (!handledSuccessfully)
				return Optional.empty();
		} else if (typeParametersPair._1 instanceof ITypeParameter typeParameter1
				&& typeParametersPair._2 instanceof ITypeParameter typeParameter2) {
			var handledSuccessfully = TypeParameterTypeParameterHandler.handle(typeParameter1, typeParameter2, state);
			if (!handledSuccessfully)
				return Optional.empty();
		} else
			return Optional.empty();

		if (state.resolved().size() >= state.inconclusive().size() + state.unknownCounter().get())
			return Optional.of(state.resolved().stream().distinct().toList());

		var result = new HashSet<Pair<IType, IType>>();
		result.addAll(state.resolved().stream().distinct().toList());

		// This is a workaround. For consistency reasons, no XCore should be used here
		var possibleTypes = jfamilycounselor.metamodel.factory.Factory.getInstance()
				.createMTypeParametersPair(typeParametersPair).possibleTypes().getElements().stream()
				.map(p -> p.getUnderlyingObject()).toList();

		var distinctInconclusives = state.inconclusive().stream().distinct().toList();

		for (Pair<IType, IType> inconclusive : distinctInconclusives) {
			if (possibleTypes.size() <= result.size())
				return Optional.of(possibleTypes);

			var inconclusiveDistinctConcreteConeProduct = distinctConcreteConeProduct(inconclusive._1, inconclusive._2);
			result.addAll(inconclusiveDistinctConcreteConeProduct.orElse(List.of()));
		}

		return Optional.of(result.stream().filter(p -> possibleTypes.contains(p)).toList());
	}

}
