package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair

import ro.lrg.jfamilycounselor.core.model.`type`.pair.TypesPair

sealed trait DerivationResult

final case class NewAssignmentsPairs(pairs: List[AssignmentsPair])
    extends DerivationResult

final case class ResolvedConcreteTypePair(pair: TypesPair)
    extends DerivationResult

final case class InconclusiveTypePair(pair: TypesPair)
    extends DerivationResult
