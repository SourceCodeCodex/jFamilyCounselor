package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.specific.two

import ro.lrg.jfamilycounselor.core.esimation.assignment.model.{Assignment, AssignmentsPair}
import ro.lrg.jfamilycounselor.core.model.call.{Instantiation, MethodCall, SuperMethodCall}
import ro.lrg.jfamilycounselor.core.model.expression.{CallExpression, ParameterExpression, ThisExpression}
import ro.lrg.jfamilycounselor.core.model.reference.This

private[pair] object ThisParameterDeriver {

  def derive(pair: AssignmentsPair): List[AssignmentsPair] = {
    val t = pair._1.expression.asInstanceOf[ThisExpression]
    val p = pair._2.expression.asInstanceOf[ParameterExpression]

    for {
      call <- p.referenceVariable.declaringMethod.calls
      (a1, a2) = (
        call match {
          case _: Instantiation =>
            Assignment(pair._1.referenceVariable, CallExpression(call), t.referenceVariable.typeUnsafe)
          case c: MethodCall =>
            val expr = c.callExpression.getOrElse(ThisExpression(This(c.methodUnsafe.underlyingJdtObject.getDeclaringType)))
            Assignment(pair._1.referenceVariable, expr, t.referenceVariable.typeUnsafe)
          case s: SuperMethodCall =>
            val expr = ThisExpression(This(s.methodUnsafe.underlyingJdtObject.getDeclaringType))
            Assignment(pair._1.referenceVariable, expr, t.referenceVariable.typeUnsafe)
        },
        Assignment(
          pair._2.referenceVariable,
          call.argumentAt(p.referenceVariable.declaringMethod.indexOf(p.referenceVariable)),
          p.referenceVariable.typeUnsafe
        )
      )
    } yield AssignmentsPair(a1, a2, pair.depth + 1, pair.combinations)
  }

}
