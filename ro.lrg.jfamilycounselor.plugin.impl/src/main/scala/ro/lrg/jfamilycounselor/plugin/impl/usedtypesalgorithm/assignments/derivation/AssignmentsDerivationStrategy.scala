package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.derivation

import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.plugin.impl.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.pair.SAssignmentsPair

trait AssignmentsDerivationStrategy {
  def derive(
      sRefPair: SRefPair[_ <: SRef],
      initialAssignmentsPairs: List[SAssignmentsPair]
  ): List[SConcreteTypePair]
}
