package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.pair

import ro.lrg.jfamilycounselor.core.model.`type`.{SConcreteTypePair, STypePair}

sealed trait AssignmentsPairDerivationResult

final case class NewAssignmentsPairs(pairs: List[SAssignmentsPair])
    extends AssignmentsPairDerivationResult

final case class ResolvedConcreteTypePair(pair: SConcreteTypePair)
    extends AssignmentsPairDerivationResult

final case class InconclusiveTypePair(pair: STypePair)
    extends AssignmentsPairDerivationResult
