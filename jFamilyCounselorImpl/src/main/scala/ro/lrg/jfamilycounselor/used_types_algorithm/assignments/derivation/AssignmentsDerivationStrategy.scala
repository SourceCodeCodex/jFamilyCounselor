package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.derivation

import ro.lrg.jfamilycounselor.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair.SAssignmentsPair

trait AssignmentsDerivationStrategy {
  def derive(
      sRefPair: SRefPair[_ <: SRef],
      initialAssignmentsPairs: List[SAssignmentsPair]
  ): List[SConcreteTypePair]
}
