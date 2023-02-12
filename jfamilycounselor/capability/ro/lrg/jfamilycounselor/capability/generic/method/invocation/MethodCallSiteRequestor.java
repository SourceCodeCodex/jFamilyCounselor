package ro.lrg.jfamilycounselor.capability.generic.method.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

class MethodCallSiteRequestor extends SearchRequestor {

    private Set<IMethod> matches = new HashSet<IMethod>();

    public List<IMethod> getMatches() {
	return List.copyOf(matches);
    }

    public void acceptSearchMatch(SearchMatch match) throws CoreException {
	if (match.getElement() != null && match.getElement() instanceof IMethod m && !m.isBinary()) {
	    matches.add((IMethod) match.getElement());
	}
    }

}
