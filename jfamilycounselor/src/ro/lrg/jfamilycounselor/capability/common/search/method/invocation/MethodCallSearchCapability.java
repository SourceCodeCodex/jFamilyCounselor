package ro.lrg.jfamilycounselor.capability.common.search.method.invocation;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

import ro.lrg.jfamilycounselor.capability.common.project.JavaProjectsCapability;
import ro.lrg.jfamilycounselor.capability.common.search.requestor.EncolsingMethodRequestor;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that resolves all methods that enclose invocations of a particular
 * method.
 * 
 * @author rosualinpetru
 *
 */
public class MethodCallSearchCapability {
    private MethodCallSearchCapability() {
    }

    private static final Cache<IMethod, List<IMethod>> cache = MonitoredUnboundedCache.getCache();

    private static final Logger logger = jFCLogger.getLogger();

    private static final SearchEngine engine = new SearchEngine();

    public static Optional<List<IMethod>> searchMethodCalls(IMethod iMethod) {
	if (cache.contains(iMethod)) {
	    return cache.get(iMethod);
	}

	if (iMethod.isLambdaMethod())
	    return Optional.empty();

	var requestor = new EncolsingMethodRequestor();
	try {
	    var searchParticipant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };

	    var pattern = SearchPattern.createPattern(iMethod, IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH);

	    var projects = JavaProjectsCapability.getJavaProjects();
	    var scope = SearchEngine.createJavaSearchScope(projects.toArray(new IJavaProject[projects.size()]), IJavaSearchScope.SOURCES);
	    engine.search(pattern, searchParticipant, scope, requestor, new NullProgressMonitor());
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();
	} catch (CoreException e) {
	    logger.warning("CoreException encountered: " + e.getMessage());
	    return Optional.empty();
	}

	var enclosingMethods = requestor.getMatches();

	cache.put(iMethod, enclosingMethods);

	return Optional.of(enclosingMethods);
    }

}