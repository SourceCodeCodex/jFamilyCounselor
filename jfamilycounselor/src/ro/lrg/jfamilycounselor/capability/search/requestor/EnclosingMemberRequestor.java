package ro.lrg.jfamilycounselor.capability.search.requestor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;

public class EnclosingMemberRequestor extends SearchRequestor {

	private Set<IMember> matches = new HashSet<IMember>();

	public List<IMember> getMatches() {
		return List.copyOf(matches);
	}

	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.getElement() != null && match.getElement() instanceof IMember m && !m.isBinary()) {
			matches.add((IMember) match.getElement());
		}
	}

}
