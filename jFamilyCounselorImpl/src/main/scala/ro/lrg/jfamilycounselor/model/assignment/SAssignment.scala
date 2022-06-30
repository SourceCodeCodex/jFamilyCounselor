package ro.lrg.jfamilycounselor.model.assignment

import ro.lrg.jfamilycounselor.model.`type`.SType
import ro.lrg.jfamilycounselor.model.expression.SExpression
import ro.lrg.jfamilycounselor.model.invocation.SInvocation
import ro.lrg.jfamilycounselor.model.ref.{SParam, SRef}

case class SAssignment(sRef: SRef, sExpression: SExpression, mostConcreteType: SType, depth: Int = 0)

object SAssignment {
  def fromSInvocation(sRef: SParam, sInvocation: SInvocation): SAssignment = {
    val index = sRef.declaringMethod.indexOf(sRef)
    val assignExpression = sInvocation.argAtIndex(index)
    SAssignment(sRef, SExpression(assignExpression), sRef.declaredType)
  }
}