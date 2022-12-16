package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignment

import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignment.visitor.LocalVariableAssigningExpressionsVisitor
import ro.lrg.jfamilycounselor.core.model.expression._
import ro.lrg.jfamilycounselor.core.util.parse.Parser

private object ExpressionDeriver {

  def derive: Expression => List[Expression] = {
    case e: LocalVariableExpression =>
      val method = e.referenceVariable.declaringMethod.underlyingJdtObject
      val methodAST = Parser.parse(method)
      val visitor = new LocalVariableAssigningExpressionsVisitor(
        e.referenceVariable.underlyingJdtObject
      )
      methodAST
        .map { ast =>
          ast.accept(visitor)
          visitor.assignedExpressions.map(Expression.apply)
        }
        .getOrElse(List())

    case e: AssignmentExpression =>
      List(Expression(e.underlyingJdtExpression.getRightHandSide))
    case e: CastExpression =>
      List(Expression(e.underlyingJdtExpression.getExpression))
    case e: ConditionalExpression =>
      List(
        Expression(e.underlyingJdtExpression.getElseExpression),
        Expression(e.underlyingJdtExpression.getElseExpression)
      )
    case e: ParenthesizedExpression =>
      List(Expression(e.underlyingJdtExpression.getExpression))
    case e => List(e)
  }

  def canBeDerived(e: Expression): Boolean = e match {
    case _: LocalVariableExpression | _: AssignmentExpression |
        _: CastExpression | _: ConditionalExpression |
        _: ParenthesizedExpression =>
      true
    case _ => false
  }
}
