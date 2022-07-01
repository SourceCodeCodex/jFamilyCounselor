package ro.lrg.jfamilycounselor.used_types_algorithm.assignments

import ro.lrg.jfamilycounselor.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.model.ref.{
  SFieldPair,
  SParamPair,
  SRef,
  SRefPair
}
import ro.lrg.jfamilycounselor.used_types_algorithm.UsedConcreteTypePairsAlgorithm0
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.combination.ParamAssignmentsCombination
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.derivation.BoundedWorkListStrategy
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair.SAssignmentsPair

object AssignmentsBasedAlgorithm extends UsedConcreteTypePairsAlgorithm0 {
  private val paramCombination = ParamAssignmentsCombination

  private val assignmentsDerivation = BoundedWorkListStrategy

  override def compute0(
      sRefPair: SRefPair[_ <: SRef]
  ): List[SConcreteTypePair] = {

    val initialAssignmentsPairs: List[SAssignmentsPair] = sRefPair match {
      case _: SFieldPair    => List()
      case pair: SParamPair => paramCombination.combine(pair)
    }

    val derivationResult =
      assignmentsDerivation.derive(sRefPair, initialAssignmentsPairs)

    derivationResult.intersect(sRefPair.possibleConcreteTypePairs0)
  }
}
