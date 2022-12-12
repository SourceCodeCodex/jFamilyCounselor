package ro.lrg.jfamilycounselor.core.model.method

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.core.model.method.call.Call
import ro.lrg.jfamilycounselor.core.model.method.call.search.StaticCallsSearch
import ro.lrg.jfamilycounselor.core.model.reference.Parameter

final case class Method(method: IMethod) {

  lazy val calls: List[Call] =
    StaticCallsSearch.findCalls(this)
  val underlyingJdtObject: IMethod = method

  def indexOf(parameter: Parameter): Int =
    underlyingJdtObject.getParameters.indexOf(parameter.underlyingJdtObject)

  override def toString: String = underlyingJdtObject.getElementName + ": Method"
}
