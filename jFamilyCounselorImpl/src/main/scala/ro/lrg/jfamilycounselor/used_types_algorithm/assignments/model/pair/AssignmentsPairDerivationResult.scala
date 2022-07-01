package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair

import ro.lrg.jfamilycounselor.model.`type`.{SConcreteTypePair, STypePair}

sealed trait AssignmentsPairDerivationResult

final case class NewAssignmentsPairs(pairs: List[SAssignmentsPair])
    extends AssignmentsPairDerivationResult

final case class ResolvedConcreteTypePair(pair: SConcreteTypePair)
    extends AssignmentsPairDerivationResult

final case class InconclusiveTypePair(pair: STypePair)
    extends AssignmentsPairDerivationResult
