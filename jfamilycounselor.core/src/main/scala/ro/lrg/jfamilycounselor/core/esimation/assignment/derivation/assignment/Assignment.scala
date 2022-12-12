package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignment

import org.eclipse.jdt.core.dom.{ConditionalExpression, ParenthesizedExpression, Assignment => JDTAssignment}
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.expression.Expression
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.ReferenceVariable

case class Assignment(
    referenceVariable: ReferenceVariable,
    expression: Expression,
    lastRecordedType: Type
) {
  lazy val resolvedConcreteType: Option[Type] = expression.`type`.filter(_.isLeafAndConcrete)

  lazy val canBeDerived: Boolean = {
    val canTheExpressionBeDerived = expression.underlyingJdtObject match {
      case _ if expression.isLocalVar => true
      case _: JDTAssignment => true
      case _: ConditionalExpression => true
      case _: ParenthesizedExpression => true
      case _ => false
    }

    expression.`type`.isDefined && canTheExpressionBeDerived
  }

  lazy val derivation: List[Assignment] = AssignmentDeriver.derive(this)
}
