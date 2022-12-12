package ro.lrg.jfamilycounselor.core.esimation.assignment

import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.esimation.assignment.combination.ParameterParameterAssignments
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.Worklist
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.`type`.pair.TypesPair
import ro.lrg.jfamilycounselor.core.model.reference.pair.{FieldFieldPair, ParameterParameterPair, ReferenceVariablesPair, ThisParameterPair}

object AssignmentsBased extends UsedTypesEstimation {

  override def compute(referenceVariablesPair: ReferenceVariablesPair): List[TypesPair] = {
    val initialAssignmentsPairs: List[AssignmentsPair] = referenceVariablesPair match {
      case p: ParameterParameterPair => ParameterParameterAssignments(p)
      case _: FieldFieldPair => List() // Still needs to be reasoned whether it is useful to implement this
      case p: ThisParameterPair => List() // TODO: implement this
    }

    val derivationResult = Worklist.apply(referenceVariablesPair, initialAssignmentsPairs)

    derivationResult.intersect(referenceVariablesPair.possibleTypes)
  }

  override def toString: String = "AssignmentsBased"
}
