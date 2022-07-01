package ro.lrg.jfamilycounselor.util.search

import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.search.SearchRequestor

import scala.collection.mutable.ListBuffer

abstract class SSearchRequestor[R <: IJavaElement]
    extends SearchRequestor {
  protected val matchesBuffer: ListBuffer[R] = ListBuffer()
  def matches: List[R] = matchesBuffer.toList
}
