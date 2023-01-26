package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair

import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

sealed trait DerivationResult

final case class NewAssignmentsPairs(pairs: Set[AssignmentsPair], inconclusive: Set[AssignmentsPair])
    extends DerivationResult

final case class ResolvedConcreteTypesPair(pair: TypesPair)
    extends DerivationResult

final case class InconclusiveTypesPair(pair: TypesPair)
    extends DerivationResult
