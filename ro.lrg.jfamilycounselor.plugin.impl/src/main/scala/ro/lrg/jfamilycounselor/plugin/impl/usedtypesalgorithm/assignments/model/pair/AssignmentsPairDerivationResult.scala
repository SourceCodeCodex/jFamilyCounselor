package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.pair

import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.{SConcreteTypePair, STypePair}

sealed trait AssignmentsPairDerivationResult

final case class NewAssignmentsPairs(pairs: List[SAssignmentsPair])
    extends AssignmentsPairDerivationResult

final case class ResolvedConcreteTypePair(pair: SConcreteTypePair)
    extends AssignmentsPairDerivationResult

final case class InconclusiveTypePair(pair: STypePair)
    extends AssignmentsPairDerivationResult
