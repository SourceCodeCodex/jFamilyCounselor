package ro.lrg.jfamilycounselor.services.cone;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.services.cache.Cache;
import ro.lrg.jfamilycounselor.services.cache.CacheService;
import ro.lrg.jfamilycounselor.services.logging.jFCLogger;

/**
 * Service that computes the concrete cone of a type (IType). The concrete cone
 * of a type is the collection of concrete types formed with that type and its
 * subtypes.
 * 
 * NOTE: The service will never compute the concrete cone for java.lang.Object.
 * 
 * @author rosualinpetru
 *
 */
public class ConcreteConeService {
    private ConcreteConeService() {
    }

    private static final int CACHE_SIZE = 128;
    private static Cache<String, List<IType>> cache = CacheService.getCache(CACHE_SIZE);

    private static Logger logger = jFCLogger.getJavaLogger();

    public Optional<List<IType>> buildConcreteCone(IType iType) {
	var typeFQN = iType.getFullyQualifiedName();

	if (cache.contains(typeFQN))
	    return cache.get(typeFQN);

	if (typeFQN.equals("java.lang.Object")) {
	    logger.info("Concrete cone computation was refused for java.lang.Object.");
	    return Optional.empty();
	}

	try {
	    var cone = Arrays.asList(iType.newTypeHierarchy(new NullProgressMonitor()).getAllSubtypes(iType));
	    cone.add(iType);

	    var concreteCone = cone.stream().filter(t -> {
		try {
		    return !(t.isAnonymous() ||
			    Flags.isInterface(t.getFlags()) ||
			    Flags.isAbstract(t.getFlags()) ||
			    Flags.isSynthetic(t.getFlags()));
		} catch (JavaModelException e) {
		    return false;
		}
	    }).toList();

	    cache.put(typeFQN, concreteCone);

	    return Optional.of(concreteCone);
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();

	}
    }

}
