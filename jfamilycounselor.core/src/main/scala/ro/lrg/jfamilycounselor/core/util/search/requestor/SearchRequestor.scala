package ro.lrg.jfamilycounselor.core.util.search.requestor

import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.search.{SearchRequestor => JSearchRequestor}

import scala.collection.mutable.ListBuffer

private[search] abstract class SearchRequestor[R <: IJavaElement] extends JSearchRequestor {
  protected val matchesBuffer: ListBuffer[R] = ListBuffer()

  def matches: List[R] = matchesBuffer.toList
}
