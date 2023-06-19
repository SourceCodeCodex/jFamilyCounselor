package ro.lrg.jfamilycounselor.approach.relevance.typeparameter;

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

    public static List<ITypeParameter> relevantTypeParameters(IType iType) {
	try {
	    return Stream.of(iType.getTypeParameters()).filter(RelevantTypeParametersUtil::isRelevant).toList();
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return List.of();
	}
    }

    private static boolean isRelevant(ITypeParameter iTypeParameter) {
	if (cache.contains(iTypeParameter))
	    return cache.get(iTypeParameter).get();

	try {
	    var isRelevant = List.of(iTypeParameter.getBounds()).isEmpty();
	    cache.put(iTypeParameter, isRelevant);
	    return isRelevant;
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return false;
	}

    }

}
