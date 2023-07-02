package ro.lrg.jfamilycounselor.capability.method;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.capability.type.SupertypeCapability;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;

/**
 * Capability that checks whether a method overrides.
 * 
 * @author rosualinpetru
 *
 */
public class MethodOverrideCapability {
	private MethodOverrideCapability() {
	}

	private static final Cache<IMethod, Boolean> cache = MonitoredUnboundedCache.getLowConsumingCache();

	public static Optional<Boolean> isMethodOverriding(IMethod iMethod) {
		if (cache.contains(iMethod))
			return cache.get(iMethod);

		var supertypesOpt = SupertypeCapability.getAllSuperTypes(iMethod.getDeclaringType());

		var isOverriding = supertypesOpt.map(supertypes -> supertypes.stream().flatMap(t -> {
			try {
				return Arrays.asList(t.getMethods()).stream();
			} catch (JavaModelException e) {
				return Stream.empty();
			}
		}).anyMatch(m -> checkSignature(m, iMethod)));

		isOverriding.ifPresent(b -> cache.put(iMethod, b));

		return isOverriding;
	}

	private static boolean checkSignature(IMethod iMethod1, IMethod iMethod2) {
		return iMethod1.getElementName().equals(iMethod2.getElementName())
				&& Arrays.asList(iMethod1.getParameterTypes()).equals(Arrays.asList(iMethod2.getParameterTypes()));
	}

}
