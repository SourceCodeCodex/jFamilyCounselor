package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.derivation

import ro.lrg.jfamilycounselor.model.`type`.{SConcreteTypePair, STypePair}
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair.SAssignmentsPair

private[derivation] case class WorkListState(
    toBeDerived: List[SAssignmentsPair],
    alreadyDerived: List[SAssignmentsPair] = List(),
    resolved: List[SConcreteTypePair] = List(),
    inconclusive: List[STypePair] = List()
)

private[derivation] case class WorkListStatePartialResult(
    toBeDerived: List[SAssignmentsPair] = List(),
    alreadyDerived: List[SAssignmentsPair],
    resolved: List[SConcreteTypePair] = List(),
    inconclusive: List[STypePair] = List()
)

object WorkListState {

  def accumulate(l: List[WorkListStatePartialResult]): WorkListState =
    l.foldLeft(WorkListState(List())) { case (acc, cs) =>
      acc.copy(
        toBeDerived = acc.toBeDerived ++ cs.toBeDerived,
        alreadyDerived = acc.alreadyDerived ++ cs.alreadyDerived,
        resolved = acc.resolved ++ cs.resolved,
        inconclusive = acc.inconclusive ++ cs.inconclusive
      )
    }

}
