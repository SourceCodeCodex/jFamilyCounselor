package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.config.AssignmentsEstimationConfig
import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.{AssignmentsPairDeriver, InconclusiveTypesPair, NewAssignmentsPairs, ResolvedConcreteTypesPair}
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import scala.concurrent.duration.DurationLong

private[assignment] object Worklist {
  def run(
           referenceVariablesPair: ReferenceVariablesPair,
           initialAssignmentsPairs: Set[AssignmentsPair]
         ): List[TypesPair] = {

    val start = Instant.now()

    @tailrec
    def work(state: State): State = {
      state match {
        case state if state.assignments.isEmpty => state
        case state if start.until(Instant.now(), ChronoUnit.MINUTES).minutes > AssignmentsEstimationConfig.TIMEOUT => state.timeout
        case state =>
          val nextState = {
            // FoldLeft does not support parallel computation, obviously
            // We need to map and then reduce on the partial states
            val partialStates = state.assignments.par.map {
              case assignmentsPair if assignmentsPair.depth > AssignmentsEstimationConfig.MAX_DEPTH =>
                state.exceedDepth(assignmentsPair)
              case assignmentsPair =>
                val derivationResult = AssignmentsPairDeriver.derive(assignmentsPair)
                derivationResult match {
                  case n: NewAssignmentsPairs => state.newAssignmentsPairs(assignmentsPair, n)
                  case r: ResolvedConcreteTypesPair => state.resolvedTypesPair(assignmentsPair, r)
                  case i: InconclusiveTypesPair => state.inconclusiveTypesPair(assignmentsPair, i)
                }
            }

            partialStates.reduce[State] { case (s1, s2) => s1.mergeWith(s2) }
          }
          work(nextState)
      }
    }

    val finalState = work(State(referenceVariablesPair, initialAssignmentsPairs))
    finalState.optimalResult.toList
  }
}
