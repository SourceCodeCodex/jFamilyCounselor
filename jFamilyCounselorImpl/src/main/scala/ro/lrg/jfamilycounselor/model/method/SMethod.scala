package ro.lrg.jfamilycounselor.model.method

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.model.invocation.SInvocation
import ro.lrg.jfamilycounselor.model.ref.SParam
import ro.lrg.jfamilycounselor.strategies.method_invocation.ztatic.StaticInvocationsSearch

final case class SMethod(method: IMethod) {

  val jdtElement: IMethod = method

  lazy val invocations: List[SInvocation] =
    StaticInvocationsSearch.findInvocations(jdtElement)

  def indexOf(sParam: SParam): Int =
    jdtElement.getParameters.indexOf(sParam.jdtElement)

  override def toString: String = jdtElement.getElementName
}
