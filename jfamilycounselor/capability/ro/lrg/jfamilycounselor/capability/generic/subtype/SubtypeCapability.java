package ro.lrg.jfamilycounselor.capability.generic.subtype;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.Constants;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class SubtypeCapability {
    private SubtypeCapability() {
    }

    private static final Cache<IType, List<String>> cache = MonitoredUnboundedCache.getCache();
    
    private static final Logger logger = jFCLogger.getJavaLogger();

    public static boolean isSubtypeOf(IType iType1, IType iType2) {
	if (iType1.getFullyQualifiedName().equals(Constants.OBJECT_FQN)) {
	    return true;
	}

	List<String> subtypesNames;
	if (cache.contains(iType1))
	    subtypesNames = cache.get(iType1).get();
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
