package ro.lrg.jfamilycounselor.approach.typeparameter.relevance;

import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.hasConcreteSubtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class RelevantTypeParametersUtil {
    private RelevantTypeParametersUtil() {
    }

    private static Cache<IJavaElement, Boolean> cache = MonitoredUnboundedCache.getLowConsumingCache();

    private static Logger logger = jFCLogger.getLogger();

    public static List<IJavaElement> relevantTypeParameters(IType iType) {
	try {
	    var result = new ArrayList<IJavaElement>();

	    // 'this' parameter
	    if (isRelevant(iType))
		result.add(iType);

	    Stream.of(iType.getTypeParameters())
		    .filter(RelevantTypeParametersUtil::isRelevant)
		    .forEach(t -> result.add(t));

	    return result;
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return List.of();
	}
    }

    private static boolean isRelevant(ITypeParameter iTypeParameter) {
	if (cache.contains(iTypeParameter))
	    return cache.get(iTypeParameter).get();

	try {
	    var bounds = List.of(iTypeParameter.getBounds());
	    var isRelevant = bounds.size() == 1 && !bounds.get(0).contains("<");

	    cache.put(iTypeParameter, isRelevant);
	    return isRelevant;
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return false;
	}

    }

    // 'this' relevance
    private static boolean isRelevant(IType t) {
	if (cache.contains(t))
	    return cache.get(t).get();

	try {
	    var result = t.getCompilationUnit() != null &&
		    !t.isAnonymous() &&
		    !t.isLambda() &&
		    (t.isClass() || t.isInterface()) &&
		    hasConcreteSubtypes(t).orElse(false);

	    cache.put(t, result);
	    return result;
	} catch (Throwable e) {
	    cache.put(t, false);
	    return false;
	}

    }

}
