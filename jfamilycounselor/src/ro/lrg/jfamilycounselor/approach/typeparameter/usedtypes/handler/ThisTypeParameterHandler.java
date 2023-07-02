package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.handler;

import static ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.util.ActualTypeParameterHandler.handleActualTypeParameter;
import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.isConcreteLeaf;
import static ro.lrg.jfamilycounselor.capability.typeparameter.TypeParameterCapability.indexOfTypeParameter;

import java.util.Optional;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;

import ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.State;
import ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.util.ParameterizedReferencesUtil;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class ThisTypeParameterHandler {
	private ThisTypeParameterHandler() {

	}

	public static boolean handle(IType thiz, ITypeParameter typeParameter, State state) {
		var parameterizedReferencesOpt = ParameterizedReferencesUtil.parameterizedReferences(thiz);

		if (parameterizedReferencesOpt.isEmpty())
			return false;

		var indexOpt = indexOfTypeParameter(typeParameter);

		if (indexOpt.isEmpty())
			return false;

		var index = indexOpt.get();

		parameterizedReferencesOpt.get().stream().forEach(s -> handleReference(s.get(), index, state));
		return true;
	}

	private static void handleReference(ParameterizedType pt, int index, State state) {
		Optional<IType> t1Opt = Optional.empty();

		if (pt.getParent() instanceof AbstractTypeDeclaration atd) {
			var je = Optional.ofNullable(atd.resolveBinding()).map(b -> b.getJavaElement());
			if (je.stream().anyMatch(t -> t instanceof IType))
				t1Opt = je.map(t -> (IType) t);
		} else
			t1Opt = handleActualTypeParameter(pt.getType());

		var typeArguments = pt.typeArguments();
		var actualParam = (Type) typeArguments.get(index);
		var t2Opt = handleActualTypeParameter(actualParam);

		if (t1Opt.isEmpty() || t2Opt.isEmpty()) {
			state.unknownCounter().incrementAndGet();
			return;
		}

		var isLeaf1 = isConcreteLeaf(t1Opt.get());
		var isLeaf2 = isConcreteLeaf(t2Opt.get());

		if (isLeaf1.isEmpty() || isLeaf2.isEmpty()) {
			state.unknownCounter().incrementAndGet();
			return;
		}

		if (isLeaf1.get() && isLeaf2.get()) {
			state.resolved().add(Pair.of(t1Opt.get(), t2Opt.get()));
			return;
		}

		state.inconclusive().add(Pair.of(t1Opt.get(), t2Opt.get()));
	}

}
