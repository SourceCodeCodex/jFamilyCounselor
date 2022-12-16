package ro.lrg.jfamilycounselor.core.model.method

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.core.model.call.Call
import ro.lrg.jfamilycounselor.core.model.method.call.search.StaticCallsSearch
import ro.lrg.jfamilycounselor.core.model.reference.Parameter

final case class Method(underlyingJdtObject: IMethod) {

  lazy val calls: List[Call] = StaticCallsSearch.findCalls(this)

  def indexOf(parameter: Parameter): Int =
    underlyingJdtObject.getParameters.indexOf(parameter.underlyingJdtObject)

  override lazy val toString: String = underlyingJdtObject.getElementName
}
