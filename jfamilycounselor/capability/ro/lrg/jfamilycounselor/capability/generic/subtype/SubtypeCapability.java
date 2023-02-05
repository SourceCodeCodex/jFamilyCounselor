package ro.lrg.jfamilycounselor.capability.generic.subtype;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.CacheManager;
import ro.lrg.jfamilycounselor.util.cache.KeyManager;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class SubtypeCapability {
    private SubtypeCapability() {
    }

    private static final Cache<String, List<String>> cache = CacheManager.getCache(2048);
    
    private static final Logger logger = jFCLogger.getJavaLogger();

    public static boolean isSubtypeOf(IType iType1, IType iType2) {
	var key = KeyManager.type(iType1);

	if (key.equals("java.lang.Object")) {
	    return true;
	}

	List<String> subtypesNames;
	if (cache.contains(key))
	    subtypesNames = cache.get(key).get();
	else
	    try {
		subtypesNames = Stream.of(iType1.newTypeHierarchy(new NullProgressMonitor()).getAllSubtypes(iType1)).map(t -> t.getFullyQualifiedName()).toList();
	    } catch (JavaModelException e) {
		logger.warning("JavaModelException encountered: " + e.getMessage());
		return false;
	    }

	return subtypesNames.contains(iType2.getFullyQualifiedName());

    }
}
