package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.handler;

import static ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.util.ActualTypeParameterHandler.handleActualTypeParameter;
import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.isConcreteLeaf;
import static ro.lrg.jfamilycounselor.capability.typeparameter.TypeParameterCapability.indexOfTypeParameter;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;

import ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.State;
import ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.util.ParameterizedReferencesUtil;
import ro.lrg.jfamilycounselor.util.datatype.Pair;

public class TypeParameterTypeParameterHandler {
	private TypeParameterTypeParameterHandler() {
	}

	public static boolean handle(ITypeParameter typeParameter1, ITypeParameter typeParameter2, State state) {
		if (!(typeParameter1.getDeclaringMember() instanceof IType)
				|| !(typeParameter2.getDeclaringMember() instanceof IType))
			return false;

		var parameterizedReferencesOpt = ParameterizedReferencesUtil
				.parameterizedReferences((IType) typeParameter1.getDeclaringMember());

		if (parameterizedReferencesOpt.isEmpty())
			return false;

		var index1Opt = indexOfTypeParameter(typeParameter1);
		var index2Opt = indexOfTypeParameter(typeParameter2);

		if (index1Opt.isEmpty() || index2Opt.isEmpty())
			return false;

		var index1 = index1Opt.get();
		var index2 = index2Opt.get();

		parameterizedReferencesOpt.get().stream().forEach(s -> handleReference(s.get(), index1, index2, state));
		return true;
	}

	private static void handleReference(ParameterizedType pt, int index1, int index2, State state) {
		var typeArguments = pt.typeArguments();

		var actualParam1 = (Type) typeArguments.get(index1);
		var actualParam2 = (Type) typeArguments.get(index2);

		var t1Opt = handleActualTypeParameter(actualParam1);
		var t2Opt = handleActualTypeParameter(actualParam2);

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
