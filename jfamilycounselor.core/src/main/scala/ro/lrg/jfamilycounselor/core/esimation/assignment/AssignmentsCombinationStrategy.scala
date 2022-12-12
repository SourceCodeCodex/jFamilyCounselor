package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.reference.pair.ReferenceVariablesPair

private[assignment] trait AssignmentsCombinationStrategy[T <: ReferenceVariablesPair] {
  def apply(referenceVariablesPair: T): List[AssignmentsPair]
}
