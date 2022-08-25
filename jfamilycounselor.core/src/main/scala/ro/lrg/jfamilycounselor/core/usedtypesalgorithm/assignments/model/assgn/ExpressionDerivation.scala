package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.assgn

import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom.{ASTNode, Assignment, CastExpression, ConditionalExpression, Expression, MethodDeclaration, ParenthesizedExpression, SimpleName}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.SExpression
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.assgn.visitor.LocalVarAssignmentsVisitor

import scala.annotation.tailrec

private[assgn] object ExpressionDerivation {

  def derive(sExpression: SExpression): List[SExpression] = {
    val expressions: List[Expression] = sExpression.expression match {
      case sn: SimpleName if sExpression.isLocalVar => deriveLocalVar(sn)
      case assignment: Assignment => deriveAssignment(assignment)
      case condExp: ConditionalExpression => deriveCondExp(condExp)
      case parExp: ParenthesizedExpression => deriveParExp(parExp)
      case cast: CastExpression => deriveCast(cast)
      case _ => List(sExpression.expression)
    }

    expressions.map(SExpression)
  }

  private def deriveLocalVar(simpleName: SimpleName): List[Expression] = {
    @tailrec
    def findMethodDec(node: ASTNode): MethodDeclaration =
      if(node.getNodeType == ASTNode.METHOD_DECLARATION)
        node.asInstanceOf[MethodDeclaration]
      else
        findMethodDec(node.getParent)

    val methodDec = findMethodDec(simpleName)
    val visitor = new LocalVarAssignmentsVisitor(simpleName.resolveBinding().getJavaElement.asInstanceOf[ILocalVariable])
    methodDec.accept(visitor)
    visitor.assignments
  }

  private def deriveAssignment(assignment: Assignment): List[Expression] = List(assignment.getRightHandSide)

  private def deriveCondExp(condExp: ConditionalExpression): List[Expression] = List(condExp.getThenExpression, condExp.getElseExpression)

  private def deriveParExp(parExp: ParenthesizedExpression): List[Expression] = List(parExp.getExpression)

  private def deriveCast(cast: CastExpression): List[Expression] = List(cast.getExpression)
}
