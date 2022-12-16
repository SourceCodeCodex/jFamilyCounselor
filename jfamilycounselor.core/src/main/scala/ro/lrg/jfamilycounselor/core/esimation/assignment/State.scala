package ro.lrg.jfamilycounselor.core.esimation.assignment

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

  lazy val optimalResult: Set[TypesPair] =
    if (resolved.nonEmpty && resolved.size >= inconclusive.size)
      resolved
    else if (inconclusive.isEmpty)
      referenceVariablesPair.possibleTypes.toSet
    else
      inconclusive
        .flatMap(_.concreteCombinations)

}
