package ro.lrg.jfamilycounselor.capability.generic.method;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.capability.generic.type.SupertypeCapability;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;

public class MethodOverrideCapability {
    private MethodOverrideCapability() {
    }
    
    private static final Cache<IMethod, Boolean> cache = MonitoredUnboundedCache.getCache();

    public static Optional<Boolean> isOverriding(IMethod iMethod) {
	if(cache.contains(iMethod))
	    return cache.get(iMethod);
	
	var supertypesOpt = SupertypeCapability.getAllSuperTypes(iMethod.getDeclaringType());
	
	var isOverriding = supertypesOpt.map(supertypes -> supertypes.stream()
		.flatMap(t -> {
		    try {
			return Arrays.asList(t.getMethods()).stream();
		    } catch (JavaModelException e) {
			return Stream.empty();
		    }
		})
		.anyMatch(m -> checkSignature(m, iMethod)));
	
	isOverriding.ifPresent(b -> cache.put(iMethod, b));

	return isOverriding;
    }

    private static boolean checkSignature(IMethod iMethod1, IMethod iMethod2) {
	return iMethod1.getElementName().equals(iMethod2.getElementName()) &&
		Arrays.asList(iMethod1.getParameterTypes()).equals(Arrays.asList(iMethod2.getParameterTypes()));
    }

}
