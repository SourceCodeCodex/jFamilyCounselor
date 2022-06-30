package ro.lrg.jfamilycounselor.util.search.requestor

import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.search.{MethodReferenceMatch, SearchMatch}
import ro.lrg.jfamilycounselor.util.search.SSearchRequestor

class MethodInvocationsRequestor
    extends SSearchRequestor[IMethod] {
  override def acceptSearchMatch(`match`: SearchMatch): Unit =
    `match` match {
      case mrm: MethodReferenceMatch if mrm.getElement.isInstanceOf[IMethod] =>
        matchesBuffer.add(mrm.getElement.asInstanceOf[IMethod])
      case _ =>
    }

}
