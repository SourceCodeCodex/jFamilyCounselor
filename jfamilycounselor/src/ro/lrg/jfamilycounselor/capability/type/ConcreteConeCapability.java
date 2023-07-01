package ro.lrg.jfamilycounselor.capability.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.Constants;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that computes the concrete cone of a type (IType). The concrete
 * cone of a type is the collection of concrete types formed with: that type and
 * its subtypes.
 * 
 * NOTE: The capability will never compute the concrete cone for
 * java.lang.Object since it is irrelevant and requires a lot of time.
 * 
 * @author rosualinpetru
 *
 */
public class ConcreteConeCapability {
    private ConcreteConeCapability() {
    }

    private static final Cache<IType, List<IType>> cache = MonitoredUnboundedCache.getLowConsumingCache();

    private static final Logger logger = jFCLogger.getLogger();

    public static Optional<List<IType>> concreteCone(IType iType) {
	if (cache.contains(iType))
	    return cache.get(iType);

	if (iType.getFullyQualifiedName().equals(Constants.OBJECT_FQN)) {
	    return Optional.empty();
	}

	try {
	    var cone = new ArrayList<>(
		    Arrays.asList(iType.newTypeHierarchy(new NullProgressMonitor()).getAllSubtypes(iType)));
	    cone.add(iType);

	    var concreteCone = cone.stream().filter(t -> {
		try {
		    return !(t.isAnnotation() || t.isAnonymous() || t.isInterface() || t.isLambda() || t.isLocal()
			    || t.isBinary() || Flags.isAbstract(t.getFlags()) || Flags.isSynthetic(t.getFlags()));
		} catch (JavaModelException e) {
		    return false;
		}
	    }).toList();

	    cache.put(iType, concreteCone);

	    return Optional.of(concreteCone);
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();

	}
    }

    public static Optional<Boolean> isConcreteLeaf(IType iType) {
	var cone = concreteCone(iType);
	return cone.map(c -> c.size() == 1 && c.contains(iType));
    }

    public static Optional<Boolean> hasConcreteSubtypes(IType iType) {
	var cone = concreteCone(iType);
	if (cone.isEmpty())
	    return Optional.empty();

	if (cone.get().contains(iType))
	    return Optional.of(cone.get().size() >= 2);

	return Optional.of(cone.get().size() >= 1);
    }
}
