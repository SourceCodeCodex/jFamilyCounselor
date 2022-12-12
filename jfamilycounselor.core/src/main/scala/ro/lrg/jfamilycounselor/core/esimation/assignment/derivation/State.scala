package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation

import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair.{AssignmentsPair, DerivationResult, InconclusiveTypePair, NewAssignmentsPairs, ResolvedConcreteTypePair}
import ro.lrg.jfamilycounselor.core.model.`type`.pair.TypesPair
import ro.lrg.jfamilycounselor.core.model.reference.pair.ReferenceVariablesPair

/**
  *
  * @param referenceVariablesPair Needed for the optimalResult computation.
  * @param assignments The assignments that will be derived in the current iteration of the algorithm.
  * @param derived The assignments that were derived so far.
  * @param resolved The types pairs that were successfully resolved and might represent a hidden correlation.
  * @param inconclusive The types for which the derivation did not successfully complete, yet we record
  *                     the last type known for the assignments.
  */
private[derivation] case class State(
                                      referenceVariablesPair: ReferenceVariablesPair,
                                      assignments: List[AssignmentsPair],
                                      derived: List[AssignmentsPair] = List(),
                                      resolved: List[TypesPair] = List(),
                                      inconclusive: List[TypesPair] = List()
                                    ) {

  val newAssignmentsPairs: List[AssignmentsPair] = assignments.diff(derived)

  val optimalResult: List[TypesPair] =
    if (resolved.nonEmpty && resolved.size >= inconclusive.size)
      resolved
    else if (inconclusive.isEmpty)
      referenceVariablesPair.possibleTypes
    else
      inconclusive
        .flatMap(_.concreteCombinations)
        .distinct

}
