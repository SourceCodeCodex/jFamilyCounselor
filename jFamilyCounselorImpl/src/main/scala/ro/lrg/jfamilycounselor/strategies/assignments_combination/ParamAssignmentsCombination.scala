package ro.lrg.jfamilycounselor.strategies.assignments_combination

import org.eclipse.jdt.core.dom.ASTNode
import ro.lrg.jfamilycounselor.model.assignment.{SAssignment, SAssignmentPair}
import ro.lrg.jfamilycounselor.model.invocation.{SClassInstanceCreation => CIC, SMethodInvocation => MI, SSuperMethodInvocation => SMI}
import ro.lrg.jfamilycounselor.model.ref.{SParam, SRefPair}

object ParamAssignmentsCombination
    extends AssignmentsCombinationStrategy[SParam] {
  override def combine(refPair: SRefPair[SParam]): List[SAssignmentPair] = for {
    i1 <- refPair._1.declaringMethod.invocations
    i2 <- refPair._2.declaringMethod.invocations
    if ((i1, i2) match {
      case (c1: CIC, c2: CIC) => c1 == c2
      //TODO: Talk with PEPI about the problem of expression
      case (m1: MI, m2: MI) => m1.callExpression.toString == m2.callExpression.toString
      case (_: SMI, _: SMI) => true
      case (s: MI, _: SMI) => s.callExpression.getNodeType == ASTNode.THIS_EXPRESSION
      case (_: SMI, s: MI) => s.callExpression.getNodeType == ASTNode.THIS_EXPRESSION
      case _ => false
    })
    (a1, a2) = (
      SAssignment.fromSInvocation(refPair._1, i1),
      SAssignment.fromSInvocation(refPair._2, i2)
    )
  } yield SAssignmentPair(a1, a2)
}
