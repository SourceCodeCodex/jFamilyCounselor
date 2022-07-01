package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair

import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom._
import ro.lrg.jfamilycounselor.model.ref.{SParam, SParamPair}
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.combination.ParamAssignmentsCombination
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.SExpression

private[pair] object ExpressionsDerivation {

  def derive(
      sExpression1: SExpression,
      sExpression2: SExpression
  ): List[(SExpression, SExpression)] = {
    val p = (sExpression1.expression, sExpression2.expression)

    val expressions: List[(Expression, Expression)] = p match {
      case (e1: SimpleName, e2: SimpleName)
          if sExpression1.isParameter && sExpression2.isParameter =>
        deriveParameters(e1, e2)
      case _ => List(p)
    }

    expressions.map(p => (SExpression(p._1), SExpression(p._2)))
  }

  private def deriveParameters(
      param1: SimpleName,
      param2: SimpleName
  ): List[(Expression, Expression)] = {
    val refPair = new SParamPair(
      SParam(
        param1.resolveBinding().getJavaElement.asInstanceOf[ILocalVariable]
      ),
      SParam(
        param2.resolveBinding().getJavaElement.asInstanceOf[ILocalVariable]
      )
    )

    if(refPair._1.declaredType0.isDefined && refPair._2.declaredType0.isDefined)
      ParamAssignmentsCombination
      .combine(refPair)
      .map(p => (p._1.sExpression.expression, p._2.sExpression.expression))
    else
      List((param1, param2))
  }

}
