package ro.lrg.jfamilycounselor.capability.generic.method.invocation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

class MethodInvocationsRequestor extends SearchRequestor {

    private List<IMethod> matches = new ArrayList<IMethod>();

    public List<IMethod> getMatches() {
	return matches;
    }

    public void acceptSearchMatch(SearchMatch match) throws CoreException {
	if (match.getElement() instanceof IMethod) {
	    matches.add((IMethod) match.getElement());
	}
    }

}
