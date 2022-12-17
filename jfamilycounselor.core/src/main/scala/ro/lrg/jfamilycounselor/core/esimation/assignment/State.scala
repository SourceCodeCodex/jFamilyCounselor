package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.{InconclusiveTypesPair, NewAssignmentsPairs, ResolvedConcreteTypesPair}
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

/** @param referenceVariablesPair Needed for the optimalResult computation.
  * @param assignments            The assignments that will be derived in the current iteration of the algorithm.
  * @param derived                The assignments that were derived so far.
  * @param resolved               The types pairs that were successfully resolved and might represent a hidden correlation.
  * @param inconclusive           The types for which the derivation did not successfully complete, yet we record
  *                               the last type known for the assignments.
  */
private[assignment] case class State(
                                      referenceVariablesPair: ReferenceVariablesPair,
                                      assignments: Set[AssignmentsPair],
                                      derived: Set[AssignmentsPair] = Set(),
                                      resolved: Set[TypesPair] = Set(),
                                      inconclusive: Set[TypesPair] = Set()
                                    ) {

  def timeout: State =
    copy(
      assignments = Set(),
      derived = derived ++ assignments,
      inconclusive = inconclusive ++ assignments.map(assignmentsPair => TypesPair(assignmentsPair._1.lastRecordedType, assignmentsPair._2.lastRecordedType))
    )

  def exceedDepth(assignmentsPair: AssignmentsPair): State =
    copy(
      assignments = assignments - assignmentsPair,
      derived = derived + assignmentsPair,
      inconclusive = inconclusive + TypesPair(assignmentsPair._1.lastRecordedType, assignmentsPair._2.lastRecordedType)
    )

  def newAssignmentsPairs(assignmentsPair: AssignmentsPair, newAssignmentsPairs: NewAssignmentsPairs): State =
    copy(
      assignments = assignments ++ (newAssignmentsPairs.pairs -- derived) - assignmentsPair,
      derived = derived + assignmentsPair,
      inconclusive = inconclusive ++ newAssignmentsPairs.inconclusive.map(p => TypesPair(p._1.lastRecordedType, p._2.lastRecordedType))
    )

  def resolvedTypesPair(assignmentsPair: AssignmentsPair, resolvedConcreteTypePair: ResolvedConcreteTypesPair): State =
    copy(
      assignments = assignments - assignmentsPair,
      derived = derived + assignmentsPair,
      resolved = resolved + resolvedConcreteTypePair.pair
    )

  def inconclusiveTypesPair(assignmentsPair: AssignmentsPair, inconclusiveTypesPair: InconclusiveTypesPair): State =
    copy(
      assignments = assignments - assignmentsPair,
      derived = derived + assignmentsPair,
      inconclusive = inconclusive + inconclusiveTypesPair.pair
    )

  def mergeWith(s: State) =
    copy(
      assignments = (assignments ++ s.assignments) -- (derived ++ s.derived),
      derived = derived ++ s.derived,
      inconclusive = inconclusive ++ s.inconclusive,
      resolved = resolved ++ s.resolved
    )


  def optimalResult: Set[TypesPair] =
    if (resolved.nonEmpty && resolved.size >= inconclusive.size)
      resolved
    else if (inconclusive.isEmpty)
      referenceVariablesPair.possibleTypes.toSet
    else
      inconclusive
        .flatMap(_.concreteCombinations)
}


