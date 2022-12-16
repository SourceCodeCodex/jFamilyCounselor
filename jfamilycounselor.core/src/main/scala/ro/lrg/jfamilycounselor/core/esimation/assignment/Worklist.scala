package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.config.AssignmentsEstimationConfig
import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.{AssignmentsPairDeriver, InconclusiveTypePair, NewAssignmentsPairs, ResolvedConcreteTypePair}
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

private[assignment] object Worklist {
  def run(
           referenceVariablesPair: ReferenceVariablesPair,
           initialAssignmentsPairs: Set[AssignmentsPair]
         ): List[TypesPair] = {
    @tailrec
    def work(state: State): State = {
      state match {
        case state if state.newAssignmentsPairs.isEmpty => state
        case state =>
          val nextState = {
            state.newAssignmentsPairs.par.foldLeft(state) {
              case (state, assignmentsPair)
                if assignmentsPair.depth > AssignmentsEstimationConfig.MAX_DEPTH =>
                state.copy(
                  derived = state.derived + assignmentsPair,
                  inconclusive = state.inconclusive + TypesPair(
                    assignmentsPair._1.lastRecordedType,
                    assignmentsPair._2.lastRecordedType
                  )
                )
              case (state, assignmentsPair) =>
                AssignmentsPairDeriver.derive(assignmentsPair) match {
                  case NewAssignmentsPairs(newAssignmentsPairs, inconclusive) =>
                    state.copy(
                      assignments = newAssignmentsPairs diff state.derived,
                      derived = state.derived + assignmentsPair,
                      inconclusive = state.inconclusive ++ inconclusive.map(p => TypesPair(p._1.lastRecordedType, p._2.lastRecordedType))
                    )
                  case ResolvedConcreteTypePair(typesPair) =>
                    state.copy(
                      resolved = state.resolved + typesPair,
                      derived = state.derived + assignmentsPair
                    )
                  case InconclusiveTypePair(typesPair) =>
                    state.copy(
                      inconclusive = state.inconclusive + typesPair,
                      derived = state.derived + assignmentsPair
                    )
                }
            }
          }
          work(nextState)
      }
    }

    val finalState = work(
      State(referenceVariablesPair, initialAssignmentsPairs)
    )
    finalState.optimalResult.toList
  }
}
