package ro.lrg.jfamilycounselor.core.esimation.assignment.combination

import ro.lrg.jfamilycounselor.core.esimation.assignment.AssignmentsCombinationStrategy
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignment.Assignment
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair.AssignmentsPair
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.expression.Expression
import ro.lrg.jfamilycounselor.core.model.method.call._
import ro.lrg.jfamilycounselor.core.model.reference.Parameter
import ro.lrg.jfamilycounselor.core.model.reference.pair.ParameterParameterPair

object ParameterParameterAssignments extends AssignmentsCombinationStrategy[ParameterParameterPair] {

  override def apply(ppPair: ParameterParameterPair): List[AssignmentsPair] =
    if (ppPair._1.declaringMethod == ppPair._2.declaringMethod)
      getParameterAssignmentsSameMethod(ppPair)
    else
      getParameterAssignmentsDifferentMethods(ppPair)

  private def getParameterAssignmentsSameMethod(ppPair: ParameterParameterPair): List[AssignmentsPair] = for {
    i <- ppPair._1.declaringMethod.calls
    (a1, a2) = (
      assignmentFromCall(ppPair._1, i),
      assignmentFromCall(ppPair._2, i)
    )
  } yield AssignmentsPair(a1, a2, 0)

  private def getParameterAssignmentsDifferentMethods(ppPair: ParameterParameterPair): List[AssignmentsPair] = for {
    c1 <- ppPair._1.declaringMethod.calls
    c2 <- ppPair._2.declaringMethod.calls if c1.isCalledOnSameObjectWith(c2)
    (a1, a2) = (
      assignmentFromCall(ppPair._1, c1),
      assignmentFromCall(ppPair._2, c2)
    )
  } yield AssignmentsPair(a1, a2, 0)

  private def assignmentFromCall(parameter: Parameter, call: Call): Assignment = {
    val index = parameter.declaringMethod.indexOf(parameter)
    val assignExpression = call.argAtIndex(index)
    Assignment(parameter, Expression(assignExpression), parameter.typeUnsafe)
  }
}
