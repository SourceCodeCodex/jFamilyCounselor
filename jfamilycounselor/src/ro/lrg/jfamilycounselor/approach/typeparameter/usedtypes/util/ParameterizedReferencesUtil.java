package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.util;

import static ro.lrg.jfamilycounselor.capability.ast.typereference.TypeReferenceCapability.extractTypeReferences;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.lazy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ParameterizedType;

public class ParameterizedReferencesUtil {
    private ParameterizedReferencesUtil() {
    }

    public static Optional<List<Supplier<ParameterizedType>>> parameterizedReferences(IType parameterizedType) {
	return extractTypeReferences(parameterizedType)
		.map(l -> l.stream()
			.map(s -> s.get())
			.filter(t -> t instanceof ParameterizedType pt && !pt.typeArguments().isEmpty())
			.map(t -> (ParameterizedType) t)
			.map(pt -> lazy(pt))
			.toList());

    }
}
