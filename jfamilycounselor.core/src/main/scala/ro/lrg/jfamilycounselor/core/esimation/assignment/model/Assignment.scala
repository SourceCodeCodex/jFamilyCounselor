package ro.lrg.jfamilycounselor.core.esimation.assignment.model

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.expression._
import ro.lrg.jfamilycounselor.core.model.reference.ReferenceVariable

case class Assignment(
    referenceVariable: ReferenceVariable,
    expression: Expression,
    lastRecordedType: Type
) {
  def resolveConcreteType: Option[Type] = expression.`type`.filter(_.isLeafAndConcrete)
}
