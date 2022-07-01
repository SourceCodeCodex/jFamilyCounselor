package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.combination

import org.eclipse.jdt.core.dom.ASTNode
import ro.lrg.jfamilycounselor.model.invocation.{
  SInvocation,
  SClassInstanceCreation => CIC,
  SMethodInvocation => MI,
  SSuperMethodInvocation => SMI
}
import ro.lrg.jfamilycounselor.model.ref.{SParam, SParamPair, SRefPair}
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.assgn.SAssignment
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair.SAssignmentsPair

object ParamAssignmentsCombination
    extends AssignmentsCombinationStrategy[SParam] {

  private def invocationToAssignment(sRef: SParam, sInvocation: SInvocation) = {
    val index = sRef.declaringMethod.indexOf(sRef)
    val assignExpression = sInvocation.argAtIndex(index)
    SAssignment(sRef, model.SExpression(assignExpression), sRef.declaredType)
  }

  override def combine(refPair: SRefPair[SParam]): List[SAssignmentsPair] =
    if (refPair._1.declaringMethod == refPair._2.declaringMethod)
      combineParamsOfSameMethod(refPair.asInstanceOf[SParamPair])
    else
      combineParamsOfDifferentMethods(refPair.asInstanceOf[SParamPair])

  private def combineParamsOfSameMethod(
      refPair: SParamPair
  ): List[SAssignmentsPair] = for {
    i <- refPair._1.declaringMethod.invocations
    (a1, a2) = (
      invocationToAssignment(refPair._1, i),
      invocationToAssignment(refPair._2, i)
    )
  } yield SAssignmentsPair(a1, a2, 0)

  private def combineParamsOfDifferentMethods(
      refPair: SParamPair
  ): List[SAssignmentsPair] = for {
    i1 <- refPair._1.declaringMethod.invocations
    i2 <- refPair._2.declaringMethod.invocations
    if ((i1, i2) match {
      case (c1: CIC, c2: CIC) => c1 == c2
      case (m1: MI, m2: MI) =>
        m1.mightBeCalledOnSameObjectOf(m2)
      case (_: SMI, _: SMI) => true
      case (s: MI, _: SMI) =>
        s.callExpression.isEmpty ||
        s.callExpression.get.getNodeType == ASTNode.THIS_EXPRESSION
      case (_: SMI, s: MI) =>
        s.callExpression.isEmpty ||
        s.callExpression.get.getNodeType == ASTNode.THIS_EXPRESSION
      case _ => false
    })
    (a1, a2) = (
      invocationToAssignment(refPair._1, i1),
      invocationToAssignment(refPair._2, i2)
    )
  } yield SAssignmentsPair(a1, a2, 0)
}
