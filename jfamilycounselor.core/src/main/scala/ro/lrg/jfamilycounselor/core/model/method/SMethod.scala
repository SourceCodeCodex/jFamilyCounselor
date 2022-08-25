package ro.lrg.jfamilycounselor.core.model.method

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.core.model.invocation.SInvocation
import ro.lrg.jfamilycounselor.core.model.method.method_invocation.ztatic.StaticInvocationsSearch
import ro.lrg.jfamilycounselor.core.model.ref.SParam

final case class SMethod(method: IMethod) {

  val jdtElement: IMethod = method

  lazy val invocations: List[SInvocation] =
    StaticInvocationsSearch.findInvocations(jdtElement)

  def indexOf(sParam: SParam): Int =
    jdtElement.getParameters.indexOf(sParam.jdtElement)

  override def toString: String = jdtElement.getElementName
}
