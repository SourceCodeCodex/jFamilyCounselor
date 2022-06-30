package ro.lrg.jfamilycounselor.util.search

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{IJavaElement, IMethod}
import org.eclipse.jdt.core.search.{IJavaSearchConstants, SearchEngine, SearchPattern, SearchRequestor}
import ro.lrg.jfamilycounselor.util.search.requestor.MethodInvocationsRequestor

//format: off
object JavaSearcher {
  import ro.lrg.jfamilycounselor.cache.implicits._

  def search[R <: IJavaElement](requestor: SSearchRequestor[R], searchPattern: SearchPattern): Set[R] = {
    val searchParticipants = Array(SearchEngine.getDefaultSearchParticipant)
    new SearchEngine().search(searchPattern, searchParticipants, SearchEngine.createWorkspaceScope, requestor, new NullProgressMonitor())
    requestor.matches
  }

  def searchMethodInvocations(method: IMethod): List[IMethod] = {
    def computeParentsMethodsOfInvoc(method: IMethod): List[IMethod] = {
      val searchPattern = SearchPattern.createPattern(method, IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH)
      val allParentMethods = JavaSearcher.search(new MethodInvocationsRequestor(), searchPattern).toList
      val validParentMethods = allParentMethods
        .filter(_.getCompilationUnit != null)

      validParentMethods
    }
    
    computeParentsMethodsOfInvoc(method).cachedBy(method)
  }
}
//format: on
