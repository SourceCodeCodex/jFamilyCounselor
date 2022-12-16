package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

object AssignmentsBased extends UsedTypesEstimation {

  override def compute(referenceVariablesPair: ReferenceVariablesPair): List[TypesPair] = {
    val selfAssignedAssignmentsPair = AssignmentsPair.selfAssigned(referenceVariablesPair)
    val derivationResult = Worklist.run(referenceVariablesPair, Set(selfAssignedAssignmentsPair))
    derivationResult.intersect(referenceVariablesPair.possibleTypes)
  }

  override lazy val toString: String = "AssignmentsBased"
}
