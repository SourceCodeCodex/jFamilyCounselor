package ro.lrg.jfamilycounselor.approach.typeparameter.usedtypes.util;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class ActualTypeParameterHandler {
	private ActualTypeParameterHandler() {
	}

	private static Logger logger = jFCLogger.getLogger();

	public static Optional<IType> handleActualTypeParameter(Type actualParameter) {
		var binding = actualParameter.resolveBinding();
		if (binding == null)
			return Optional.empty();

		if (!binding.isFromSource() || binding.isCapture() || binding.isPrimitive() || binding.isAnnotation()
				|| binding.isAnonymous() || binding.isArray() || binding.isIntersectionType() || binding.isNullType())
			return Optional.empty();

		if (binding.isGenericType() || binding.isParameterizedType() || binding.isRawType() || binding.isClass()
				|| binding.isInterface() || binding.isEnum()) {
			var je = binding.getJavaElement();
			if (je != null && je instanceof IType t)
				return Optional.ofNullable(t);
			return Optional.empty();
		}

		if (binding.isTypeVariable()) {
			var bounds = List.of(binding.getTypeBounds());
			if (bounds.size() == 1) {
				var je = bounds.get(0).getJavaElement();
				if (je != null && je instanceof IType t)
					return Optional.ofNullable(t);
			}
			return Optional.empty();
		}

		if (binding.isWildcardType() && binding.isUpperbound())
			return handleActualTypeParameter(((WildcardType) actualParameter).getBound());

		logger.warning("Unhandled binding: " + binding.getClass().getCanonicalName());
		return Optional.empty();
	}
}
