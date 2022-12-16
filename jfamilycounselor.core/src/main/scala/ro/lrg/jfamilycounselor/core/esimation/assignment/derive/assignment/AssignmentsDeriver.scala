package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.model.Assignment
import ro.lrg.jfamilycounselor.core.model.expression.Expression

object AssignmentsDeriver {
  def derive: Assignment => List[Assignment] = assignment => {
    def internal(
                  assignment: Assignment,
                  derived: List[Expression]
                ): List[Assignment] = {
      if (!ExpressionDeriver.canBeDerived(assignment.expression))
        List(assignment)
      else
        ExpressionDeriver
          .derive(assignment.expression)
          .filterNot(derived.contains)
          .map(expression =>
            Assignment(
              assignment.referenceVariable,
              expression,
              assignment.expression.`type`.get
            )
          )
          .flatMap(newAssignment =>
            internal(newAssignment, newAssignment.expression :: derived)
          )
    }

    internal(assignment, List())
  }

  def canBeDerived(a: Assignment): Boolean = ExpressionDeriver.canBeDerived(a.expression)
}
