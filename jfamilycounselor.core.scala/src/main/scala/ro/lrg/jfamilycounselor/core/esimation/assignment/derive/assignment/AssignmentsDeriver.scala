package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.model.Assignment
import ro.lrg.jfamilycounselor.core.model.expression.Expression

import scala.annotation.tailrec

object AssignmentsDeriver {
  def derive: Assignment => List[Assignment] = assignment => {
    @tailrec
    def internal(
                  assignments: List[Assignment],
                  accumulator: List[Assignment] = List(),
                  derived: List[Expression] = List()
                ): List[Assignment] = {
      val (toBeDerived, noLongerDerivable) = assignments.partition(canBeDerived)

      if (toBeDerived.isEmpty) {
        accumulator ++ noLongerDerivable
      } else {
        val derivedAssignments = for {
          assignment <- toBeDerived if !derived.contains(assignment.expression)
          derivedExpression <- ExpressionDeriver.derive(assignment.expression)
        } yield Assignment(
          assignment.referenceVariable,
          derivedExpression,
          assignment.expression.`type`.filterNot(_.isObjectType).getOrElse(assignment.lastRecordedType)
        )

        internal(derivedAssignments, accumulator ++ noLongerDerivable, toBeDerived.map(_.expression) ++ derived)
      }
    }

    internal(List(assignment))
  }

  def canBeDerived(a: Assignment): Boolean = ExpressionDeriver.canBeDerived(a.expression)
}
