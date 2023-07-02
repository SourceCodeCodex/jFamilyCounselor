package ro.lrg.jfamilycounselor.capability.type;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class SupertypeCapability {
	private SupertypeCapability() {
	}

	private static final Cache<IType, List<IType>> cache = MonitoredUnboundedCache.getLowConsumingCache();

	private static final Logger logger = jFCLogger.getLogger();

	public static Optional<List<IType>> getAllSuperTypes(IType iType) {
		if (cache.contains(iType))
			return cache.get(iType);

		try {
			var superTypes = Arrays.asList(iType.newSupertypeHierarchy(new NullProgressMonitor()).getSupertypes(iType));
			cache.put(iType, superTypes);
			return Optional.of(superTypes);
		} catch (JavaModelException e) {
			logger.warning("JavaModelException encountered: " + e.getMessage());
		}

		return Optional.empty();

	}
}
