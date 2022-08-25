package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments

import ro.lrg.jfamilycounselor.core.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.core.model.ref.{SFieldPair, SParamPair, SRef, SRefPair}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.UsedConcreteTypePairsAlgorithm0
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.combination.ParamAssignmentsCombination
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.derivation.BoundedWorkListStrategy
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.pair.SAssignmentsPair

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
