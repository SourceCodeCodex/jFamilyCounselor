package ro.lrg.jfamilycounselor.capability.search.type;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;

import ro.lrg.jfamilycounselor.capability.project.JavaProjectsCapability;
import ro.lrg.jfamilycounselor.capability.search.requestor.EnclosingMemberRequestor;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class TypeReferenceSearchCapability {
    private TypeReferenceSearchCapability() {

    }

    private static final Cache<IType, List<IMember>> cache = MonitoredUnboundedCache.getLowConsumingCache();

    private static final Logger logger = jFCLogger.getLogger();

    private static final SearchEngine engine = new SearchEngine();

    public static Optional<List<IMember>> searchTypeReferences(IType iType) {
	if (cache.contains(iType)) {
	    return cache.get(iType);
	}

	var requestor = new EnclosingMemberRequestor();

	try {
	    var searchParticipant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };

	    var pattern = SearchPattern.createPattern(iType, IJavaSearchConstants.REFERENCES);

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

	var enclosingMembers = requestor.getMatches();

	cache.put(iType, enclosingMembers);

	return Optional.of(enclosingMembers);
    }

}