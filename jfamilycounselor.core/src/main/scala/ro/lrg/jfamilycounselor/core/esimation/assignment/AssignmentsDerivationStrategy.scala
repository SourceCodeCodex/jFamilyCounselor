package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.`type`.pair.TypesPair
import ro.lrg.jfamilycounselor.core.model.reference.pair.ReferenceVariablesPair

private[assignment] trait AssignmentsDerivationStrategy {
  def apply(referenceVariable: ReferenceVariablesPair, initialAssignmentsPairs: List[AssignmentsPair]): List[TypesPair]
}
