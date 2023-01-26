package ro.lrg.jfamilycounselor.core.util.search.requestor

import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.search.{MethodReferenceMatch, SearchMatch}

private[search] class MethodInvocationsRequestor extends SearchRequestor[IMethod] {
  override def acceptSearchMatch(`match`: SearchMatch): Unit =
    `match` match {
      case mrm: MethodReferenceMatch if mrm.getElement.isInstanceOf[IMethod] =>
        matchesBuffer.addOne(mrm.getElement.asInstanceOf[IMethod])
      case _ =>
    }
}
