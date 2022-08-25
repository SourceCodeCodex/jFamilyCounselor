package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.derivation

import ro.lrg.jfamilycounselor.core.model.`type`.{SConcreteTypePair, STypePair}
import ro.lrg.jfamilycounselor.core.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.derivation
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.pair.{InconclusiveTypePair, NewAssignmentsPairs, ResolvedConcreteTypePair, SAssignmentsPair}

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import scala.concurrent.duration._

object BoundedWorkListStrategy extends AssignmentsDerivationStrategy {
  private val MAX_DEPTH_THRESHOLD = 3
  private val MAX_DURATION = 30.minutes

  override def derive(
      sRefPair: SRefPair[_ <: SRef],
      initialAssignmentsPairs: List[SAssignmentsPair]
  ): List[SConcreteTypePair] = {
    val startTime = System.nanoTime().nanos

    @tailrec
    def workListAlgorithm(state: WorkListState): WorkListState = {
      val newTime = System.nanoTime().nanos
      if (state.toBeDerived.diff(state.alreadyDerived).isEmpty || newTime - startTime >= MAX_DURATION) {
        state
      } else {

        val stateDtos = state.toBeDerived
          .diff(state.alreadyDerived)
          .par
          .map { case p @ SAssignmentsPair(_1, _2, depth) =>
            //format: off
            if (depth > MAX_DEPTH_THRESHOLD)
              WorkListStatePartialResult(inconclusive = STypePair(_1.mostConcreteType, _2.mostConcreteType) :: state.inconclusive, alreadyDerived = List(p))
            else
              p.derivationResult match {
                case NewAssignmentsPairs(pairs) =>
                  WorkListStatePartialResult(toBeDerived = pairs, alreadyDerived = List(p))
                case ResolvedConcreteTypePair(pair) =>
                  WorkListStatePartialResult(resolved = List(pair), alreadyDerived =  List(p))
                case InconclusiveTypePair(pair) =>
                  derivation.WorkListStatePartialResult(inconclusive = List(pair), alreadyDerived =  List(p))
              }
            //format: on
          }
          .toList

        val stateUpdate = WorkListState.accumulate(stateDtos)

        val newState = state.copy(
          toBeDerived = stateUpdate.toBeDerived,
          alreadyDerived = state.alreadyDerived ++ stateUpdate.alreadyDerived,
          resolved = state.resolved ++ stateUpdate.resolved,
          inconclusive = state.inconclusive ++ stateUpdate.inconclusive
        )

        workListAlgorithm(newState)
      }
    }

    val state = workListAlgorithm(WorkListState(initialAssignmentsPairs))

    if (
      state.resolved.nonEmpty && state.resolved.size >= state.inconclusive.size
    )
      state.resolved
    else {
      if (state.inconclusive.isEmpty)
        sRefPair.possibleConcreteTypePairs0
      else
        state.inconclusive
          .flatMap(_.allConcreteCombinations)
          .distinct
    }
  }
}
