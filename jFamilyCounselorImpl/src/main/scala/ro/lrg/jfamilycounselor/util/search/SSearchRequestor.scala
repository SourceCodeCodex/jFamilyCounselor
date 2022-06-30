package ro.lrg.jfamilycounselor.util.search

import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.search.SearchRequestor

abstract class SSearchRequestor[R <: IJavaElement]
    extends SearchRequestor {
  protected val matchesBuffer: scala.collection.mutable.Set[R] =
    scala.collection.mutable.Set()
  def matches: Set[R] = matchesBuffer.toSet
}
