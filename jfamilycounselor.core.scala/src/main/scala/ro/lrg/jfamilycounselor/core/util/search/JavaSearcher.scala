package ro.lrg.jfamilycounselor.core.util.search

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.search.{IJavaSearchConstants, SearchEngine, SearchPattern}
import org.eclipse.jdt.core.{IJavaElement, IMethod}
import ro.lrg.jfamilycounselor.core.util.search.requestor.{MethodInvocationsRequestor, SearchRequestor}

//format: off
object JavaSearcher {
  def searchMethodInvocations(method: IMethod): List[IMethod] = {
    val searchPattern = SearchPattern.createPattern(method, IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH)
    val allParentMethods = JavaSearcher.search(new MethodInvocationsRequestor(), searchPattern)
    val validParentMethods = allParentMethods.filter(_.getCompilationUnit != null)
    validParentMethods
  }

  private def search[R <: IJavaElement](requestor: SearchRequestor[R], searchPattern: SearchPattern): List[R] = {
    val searchParticipants = Array(SearchEngine.getDefaultSearchParticipant)
    new SearchEngine().search(searchPattern, searchParticipants, SearchEngine.createWorkspaceScope, requestor, new NullProgressMonitor())
    requestor.matches
  }
}
//format: on
