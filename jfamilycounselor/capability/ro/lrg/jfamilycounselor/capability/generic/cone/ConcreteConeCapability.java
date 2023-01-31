package ro.lrg.jfamilycounselor.capability.generic.cone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.CacheService;
import ro.lrg.jfamilycounselor.util.cache.KeyManager;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Service that computes the concrete cone of a type (IType). The concrete cone
 * of a type is the collection of concrete types formed with that type and its
 * subtypes.
 * 
 * NOTE: The service will never compute the concrete cone for java.lang.Object
 * since it is irrelevant and requires a lot of time.
 * 
 * @author rosualinpetru
 *
 */
public class ConcreteConeCapability {
    private ConcreteConeCapability() {
    }

    private static final Cache<String, List<IType>> cache = CacheService.getCache(2048);

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static Optional<List<IType>> concreteCone(IType iType) {
	var key = KeyManager.type(iType);

	if (cache.contains(key))
	    return cache.get(key);

	if (key.equals("java.lang.Object")) {
	    logger.info("Concrete cone computation was refused for java.lang.Object.");
	    return Optional.empty();
	}

	try {
	    var cone = new ArrayList<>(Arrays.asList(iType.newTypeHierarchy(new NullProgressMonitor()).getAllSubtypes(iType)));
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

	    cache.put(key, concreteCone);

	    return Optional.of(concreteCone);
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();

	}
    }

}
