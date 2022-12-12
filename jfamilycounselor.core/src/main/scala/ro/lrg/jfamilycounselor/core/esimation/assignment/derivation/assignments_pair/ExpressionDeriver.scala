package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair

import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom.{Expression => JDTExpression, _}
import ro.lrg.jfamilycounselor.core.esimation.assignment.combination.ParameterParameterAssignments
import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.expression.Expression
import ro.lrg.jfamilycounselor.core.model.reference.Parameter
import ro.lrg.jfamilycounselor.core.model.reference.pair.ParameterParameterPair

private object ExpressionDeriver {
  def derive(e1: Expression, e2: Expression): List[(Expression, Expression)] = {

    val expressions: List[(JDTExpression, JDTExpression)] =
      if (e1.isParameter && e2.isParameter)
        JDTImpl.derive(e1.underlyingJdtObject.asInstanceOf[SimpleName], e2.underlyingJdtObject.asInstanceOf[SimpleName])
      else
        List((e1.underlyingJdtObject, e2.underlyingJdtObject))

    expressions.map(p => (Expression(p._1), Expression(p._2)))
  }

  private object JDTImpl {

    def derive(param1: SimpleName, param2: SimpleName): List[(JDTExpression, JDTExpression)] = {
      val pair = ParameterParameterPair(
        Parameter(
          param1.resolveBinding().getJavaElement.asInstanceOf[ILocalVariable]
        ),
        Parameter(
          param2.resolveBinding().getJavaElement.asInstanceOf[ILocalVariable]
        )
      )

      if (pair._1.`type`.isDefined && pair._2.`type`.isDefined)
        ParameterParameterAssignments(pair).map(p => (p._1.expression.underlyingJdtObject, p._2.expression.underlyingJdtObject))
      else
        List((param1, param2))
    }
  }

}
