package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.derivation

import ro.lrg.jfamilycounselor.core.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.core.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.pair.SAssignmentsPair

trait AssignmentsDerivationStrategy {
  def derive(
      sRefPair: SRefPair[_ <: SRef],
      initialAssignmentsPairs: List[SAssignmentsPair]
  ): List[SConcreteTypePair]
}
