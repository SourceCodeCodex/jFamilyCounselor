package ro.lrg.jfamilycounselor.used_types_algorithm.assignments

import ro.lrg.jfamilycounselor.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.model.ref.{SFieldPair, SParamPair, SRef, SRefPair}
import ro.lrg.jfamilycounselor.strategies.assignments_combination.ParamAssignmentsCombination
import ro.lrg.jfamilycounselor.used_types_algorithm.UsedConcreteTypePairsAlgorithm0

object AssignmentsBasedAlgorithm extends UsedConcreteTypePairsAlgorithm0 {
  override def compute0(
      refPair: SRefPair[_ <: SRef]
  ): List[SConcreteTypePair] = {

    val assignmentsPairs = refPair match {
      case _: SFieldPair => List()
      case pair: SParamPair => ParamAssignmentsCombination.combine(pair)
    }

    assignmentsPairs.foreach(println)


    List()
  }
}
