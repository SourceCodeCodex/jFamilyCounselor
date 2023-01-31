package ro.lrg.jfamilycounselor.capability.generic.search.method;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.CacheService;
import ro.lrg.jfamilycounselor.util.cache.KeyManager;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class MethodSearchCapability {
    private MethodSearchCapability() {
    }

    private static final Cache<String, List<IMethod>> cache = CacheService.getCache(2048);

    private static final Logger logger = jFCLogger.getJavaLogger();

    private static final SearchEngine engine = new SearchEngine();

    public Optional<List<IMethod>> searchMethodInvocations(IMethod iMethod) {
	var key = KeyManager.method(iMethod);

	if (cache.contains(key)) {
	    return cache.get(key);
	}

	SearchPattern pattern = SearchPattern.createPattern(iMethod, IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH);

	Optional<List<IMethod>> allParentMethods = search(new MethodInvocationsRequestor(), pattern);
	var parentMethods = allParentMethods.map(o -> o.stream().filter(m -> m.getCompilationUnit() != null).collect(Collectors.toList()));

	parentMethods.ifPresent(m -> cache.put(key, m));

	return parentMethods;
    }

    private Optional<List<IMethod>> search(MethodInvocationsRequestor requestor, SearchPattern pattern) {
	SearchParticipant[] searchParticipant = { SearchEngine.getDefaultSearchParticipant() };

	try {
	    engine.search(pattern, searchParticipant, SearchEngine.createWorkspaceScope(), requestor, new NullProgressMonitor());
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();
	} catch (CoreException e) {
	    logger.warning("CoreException encountered: " + e.getMessage());
	    return Optional.empty();
	}

	return Optional.of(requestor.getMatches());
    }
}