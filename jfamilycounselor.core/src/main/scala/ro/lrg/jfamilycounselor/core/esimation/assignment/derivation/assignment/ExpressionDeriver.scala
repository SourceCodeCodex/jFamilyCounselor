package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignment

import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom.{Expression => JDTExpression, _}
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.expression.Expression
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.visitor.LocalVariableAssigningExpressionsVisitor

import scala.annotation.tailrec

private object ExpressionDeriver {
  def derive(expression: Expression): List[Expression] = {

    val jdtExpressions: List[JDTExpression] = expression.underlyingJdtObject match {
      case sn: SimpleName if expression.isLocalVar => JDTHandler.derive(sn)
      case assignment: Assignment => JDTHandler.derive(assignment)
      case condExp: ConditionalExpression => JDTHandler.derive(condExp)
      case parExp: ParenthesizedExpression => JDTHandler.derive(parExp)
      case cast: CastExpression => JDTHandler.derive(cast)
      case _ => List(expression.underlyingJdtObject)
    }

    jdtExpressions.map(Expression)
  }

  private object JDTHandler {
    def derive(simpleName: SimpleName): List[JDTExpression] = {
      @tailrec
      def findMethodDeclaration(node: ASTNode): MethodDeclaration =
        if (node.getNodeType == ASTNode.METHOD_DECLARATION)
          node.asInstanceOf[MethodDeclaration]
        else
          findMethodDeclaration(node.getParent)

      val methodDec = findMethodDeclaration(simpleName)
      val visitor = new LocalVariableAssigningExpressionsVisitor(simpleName.resolveBinding().getJavaElement.asInstanceOf[ILocalVariable])
      methodDec.accept(visitor)
      visitor.assignedExpressions
    }

    def derive(assignment: Assignment): List[JDTExpression] = List(assignment.getRightHandSide)

    def derive(condExp: ConditionalExpression): List[JDTExpression] = List(condExp.getThenExpression, condExp.getElseExpression)

    def derive(parExp: ParenthesizedExpression): List[JDTExpression] = List(parExp.getExpression)

    def derive(cast: CastExpression): List[JDTExpression] = List(cast.getExpression)
  }

}
