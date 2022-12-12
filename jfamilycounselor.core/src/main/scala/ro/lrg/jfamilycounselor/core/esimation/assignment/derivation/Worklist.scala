package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation

import ro.lrg.jfamilycounselor.core.esimation.assignment.AssignmentsDerivationStrategy
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair.{AssignmentsPair, InconclusiveTypePair, NewAssignmentsPairs, ResolvedConcreteTypePair}
import ro.lrg.jfamilycounselor.core.model.`type`.pair.TypesPair
import ro.lrg.jfamilycounselor.core.model.reference.pair.ReferenceVariablesPair

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

object Worklist extends AssignmentsDerivationStrategy {
  private val MAX_DEPTH_THRESHOLD = 3

  override def apply(referenceVariablesPair: ReferenceVariablesPair, initialAssignmentsPairs: List[AssignmentsPair]): List[TypesPair] = {
    @tailrec
    def work(state: State): State = {
      state match {
        case state if state.newAssignmentsPairs.isEmpty => state
        case state =>
          val nextState = {
            state.newAssignmentsPairs.sortBy(- _.depth).par.foldLeft(state) {
              case (state, assignmentsPair) if assignmentsPair.depth > MAX_DEPTH_THRESHOLD =>
                state.copy(
                  derived = assignmentsPair :: state.derived,
                  inconclusive = TypesPair(assignmentsPair._1.lastRecordedType, assignmentsPair._2.lastRecordedType) :: state.inconclusive,
                )
              case (state, assignmentsPair) =>
                assignmentsPair.derivation match {
                  case NewAssignmentsPairs(newAssignmentsPairs) =>
                    state.copy(assignments = newAssignmentsPairs, derived = assignmentsPair :: state.derived)
                  case ResolvedConcreteTypePair(typesPair) =>
                    state.copy(resolved = typesPair :: state.resolved, derived = assignmentsPair :: state.derived)
                  case InconclusiveTypePair(typesPair) =>
                    state.copy(inconclusive = typesPair :: state.inconclusive, derived = assignmentsPair :: state.derived)
                }
            }
          }
          work(nextState)
      }
    }

    val finalState = work(State(referenceVariablesPair, initialAssignmentsPairs))
    finalState.optimalResult
  }
}
