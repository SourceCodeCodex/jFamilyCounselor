package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair

import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignment.Assignment

case class AssignmentsPair(_1: Assignment, _2: Assignment, depth: Int) {
  lazy val canBeDerived: Boolean =
    (_1.canBeDerived || _1.expression.isParameter) && (_2.canBeDerived || _2.expression.isParameter)

  lazy val derivation: DerivationResult = AssignmentsPairDeriver.derive(this)
}
