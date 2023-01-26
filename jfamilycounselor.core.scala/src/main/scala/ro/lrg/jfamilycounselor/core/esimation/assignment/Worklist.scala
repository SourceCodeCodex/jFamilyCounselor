package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.config.AssignmentsEstimationConfig
import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.{AssignmentsPairDeriver, InconclusiveTypesPair, NewAssignmentsPairs, ResolvedConcreteTypesPair}
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

import scala.annotation.tailrec

private[assignment] object Worklist {
  def run(
           referenceVariablesPair: ReferenceVariablesPair,
           initialAssignmentsPair: AssignmentsPair
         ): List[TypesPair] = {

    @tailrec
    def work(state: State): State = {
      state match {
        case state if state.assignmentsPairs.isEmpty => state
        case state =>
          val nextState =
            state.assignmentsPairs.foldLeft(state) {
              case (accState, assignmentsPair) if assignmentsPair.depth > AssignmentsEstimationConfig.MAX_DEPTH =>
                accState.invalidate(assignmentsPair)
              case (accState, assignmentsPair) =>
                AssignmentsPairDeriver.derive(assignmentsPair) match {
                  case n: NewAssignmentsPairs => accState.newAssignmentsPairs(assignmentsPair, n)
                  case r: ResolvedConcreteTypesPair => accState.resolvedTypesPair(assignmentsPair, r)
                  case i: InconclusiveTypesPair => accState.inconclusiveTypesPair(assignmentsPair, i)
                }
            }

          work(nextState)
      }
    }

    val finalState = work(State(referenceVariablesPair, Set(initialAssignmentsPair)))
    finalState.optimalResult.toList
  }
}

