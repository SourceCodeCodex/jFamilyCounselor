package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.{InconclusiveTypesPair, NewAssignmentsPairs, ResolvedConcreteTypesPair}
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

/** @param resolved     The types pairs that were successfully resolved and might represent a hidden correlation.
  * @param inconclusive The types for which the derivation did not successfully complete, yet we record
  *                     the last type known for the assignments.
  */
private[assignment] case class State(
                                      referenceVariablesPair: ReferenceVariablesPair,
                                      assignmentsPairs: Set[AssignmentsPair],
                                      resolved: Set[TypesPair] = Set(),
                                      inconclusive: Set[TypesPair] = Set()
                                    ) {

  def invalidate(assignmentsPair: AssignmentsPair): State =
    copy(
      assignmentsPairs = assignmentsPairs - assignmentsPair,
      inconclusive = inconclusive + TypesPair(assignmentsPair._1.lastRecordedType, assignmentsPair._2.lastRecordedType)
    )

  def newAssignmentsPairs(assignmentsPair: AssignmentsPair, newAssignmentsPairs: NewAssignmentsPairs): State =
    copy(
      assignmentsPairs = assignmentsPairs ++ newAssignmentsPairs.pairs - assignmentsPair,
      inconclusive = inconclusive ++ newAssignmentsPairs.inconclusive.map(p => TypesPair(p._1.lastRecordedType, p._2.lastRecordedType))
    )

  def resolvedTypesPair(assignmentsPair: AssignmentsPair, resolvedConcreteTypePair: ResolvedConcreteTypesPair): State =
    copy(
      assignmentsPairs = assignmentsPairs - assignmentsPair,
      resolved = resolved + resolvedConcreteTypePair.pair
    )

  def inconclusiveTypesPair(assignmentsPair: AssignmentsPair, inconclusiveTypesPair: InconclusiveTypesPair): State =
    copy(assignmentsPairs = assignmentsPairs - assignmentsPair,
      inconclusive = inconclusive + inconclusiveTypesPair.pair)


  def optimalResult: Set[TypesPair] =
    if (resolved.nonEmpty && resolved.size >= inconclusive.size)
      resolved
    else if (inconclusive.isEmpty)
      referenceVariablesPair.possibleTypes.toSet
    else
      inconclusive
        .flatMap(_.concreteCombinations)
}


