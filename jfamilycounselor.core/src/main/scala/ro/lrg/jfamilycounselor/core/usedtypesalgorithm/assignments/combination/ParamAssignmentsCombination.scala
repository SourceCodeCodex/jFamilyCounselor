package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.combination

import org.eclipse.jdt.core.dom.ASTNode
import ro.lrg.jfamilycounselor.core.model.invocation.{SClassInstanceCreation, SInvocation, SMethodInvocation, SSuperMethodInvocation}
import ro.lrg.jfamilycounselor.core.model.ref.{SParam, SParamPair, SRefPair}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.assgn.SAssignment
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.{SExpression, pair}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.pair.SAssignmentsPair

object ParamAssignmentsCombination
    extends AssignmentsCombinationStrategy[SParam] {

  private def invocationToAssignment(sRef: SParam, sInvocation: SInvocation) = {
    val index = sRef.declaringMethod.indexOf(sRef)
    val assignExpression = sInvocation.argAtIndex(index)
    SAssignment(sRef, SExpression(assignExpression), sRef.declaredType)
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
  } yield pair.SAssignmentsPair(a1, a2, 0)

  private def combineParamsOfDifferentMethods(
      refPair: SParamPair
  ): List[SAssignmentsPair] = for {
    i1 <- refPair._1.declaringMethod.invocations
    i2 <- refPair._2.declaringMethod.invocations
    if ((i1, i2) match {
      case (c1: SClassInstanceCreation, c2: SClassInstanceCreation) => c1 == c2
      case (m1: SMethodInvocation, m2: SMethodInvocation) =>
        m1.mightBeCalledOnSameObjectOf(m2)
      case (_: SSuperMethodInvocation, _: SSuperMethodInvocation) => true
      case (s: SMethodInvocation, _: SSuperMethodInvocation) =>
        s.callExpression.isEmpty ||
        s.callExpression.get.getNodeType == ASTNode.THIS_EXPRESSION
      case (_: SSuperMethodInvocation, s: SMethodInvocation) =>
        s.callExpression.isEmpty ||
        s.callExpression.get.getNodeType == ASTNode.THIS_EXPRESSION
      case _ => false
    })
    (a1, a2) = (
      invocationToAssignment(refPair._1, i1),
      invocationToAssignment(refPair._2, i2)
    )
  } yield pair.SAssignmentsPair(a1, a2, 0)
}
