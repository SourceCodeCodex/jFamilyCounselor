package ro.lrg.jfamilycounselor.core.esimation

import ro.lrg.jfamilycounselor.core.esimation.assignment.AssignmentsBased
import ro.lrg.jfamilycounselor.core.esimation.name.NameBased
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

trait UsedTypesEstimation {
  def compute(referenceVariablesPair: ReferenceVariablesPair): List[TypesPair]
}

object UsedTypesEstimation {
  val NAME_BASED: UsedTypesEstimation = NameBased
  val ASSIGNMENTS_BASED: UsedTypesEstimation = AssignmentsBased
}
