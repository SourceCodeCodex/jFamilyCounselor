package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.specific.two

import ro.lrg.jfamilycounselor.core.esimation.assignment.config.AssignmentsEstimationConfig
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.call.{Call, Instantiation, MethodCall, SuperMethodCall}
import ro.lrg.jfamilycounselor.core.model.expression.{FieldExpression, LocalVariableExpression, ParameterExpression}

private[pair] object ParameterParameterDeriver {

  def derive(pair: AssignmentsPair): List[AssignmentsPair] = {
    val e1 = pair._1.expression.asInstanceOf[ParameterExpression]
    val e2 = pair._2.expression.asInstanceOf[ParameterExpression]

    if (e1.referenceVariable.declaringMethod == e2.referenceVariable.declaringMethod)
      for {
        call <- e1.referenceVariable.declaringMethod.calls
        (a1, a2) = (
          pair._1.copy(expression = call.argumentAt(e1.referenceVariable.declaringMethod.indexOf(e1.referenceVariable)), lastRecordedType = e1.referenceVariable.typeUnsafe),
          pair._2.copy(expression = call.argumentAt(e2.referenceVariable.declaringMethod.indexOf(e2.referenceVariable)), lastRecordedType = e2.referenceVariable.typeUnsafe)
        )
      } yield AssignmentsPair(a1, a2, pair.depth + 1, pair.combinations)
    else if (pair.combinations < AssignmentsEstimationConfig.MAX_COMBINATIONS)
      for {
        c1 <- e1.referenceVariable.declaringMethod.calls
        c2 <- e2.referenceVariable.declaringMethod.calls if calledOnSameObject(c1, c2)
        (a1, a2) = (
          pair._1.copy(expression = c1.argumentAt(e1.referenceVariable.declaringMethod.indexOf(e1.referenceVariable)), lastRecordedType = e1.referenceVariable.typeUnsafe),
          pair._2.copy(expression = c2.argumentAt(e2.referenceVariable.declaringMethod.indexOf(e2.referenceVariable)), lastRecordedType = e2.referenceVariable.typeUnsafe)
        )
      } yield AssignmentsPair(a1, a2, pair.depth + 1, pair.combinations + 1)
    else
      List(pair)
  }

  // Needs improvements - it is not accurate.
  def calledOnSameObject(c1: Call, c2: Call): Boolean =
    c1 match {
      case i: Instantiation => i == c2
      case m1: MethodCall => c2 match {
        case m2: MethodCall =>
          if (m1.callExpression.isEmpty && m2.callExpression.isEmpty)
            m1.method.map(_.underlyingJdtObject.getDeclaringType) == m2.method.map(_.underlyingJdtObject.getDeclaringType)
          else
            (for {
              e1 <- m1.callExpression
              e2 <- m2.callExpression
            } yield (e1, e2) match {
              case (l1: LocalVariableExpression, l2: LocalVariableExpression) => l1 == l2
              case (f1: FieldExpression, f2: FieldExpression) => f1 == f2
              case (p1: ParameterExpression, p2: ParameterExpression) => p1 == p2
              case _ => false
            }).getOrElse(false)
        case _ => false
      }
      case s1: SuperMethodCall => c2 match {
        case s2: SuperMethodCall => s1.method.map(_.underlyingJdtObject.getDeclaringType) == s2.method.map(_.underlyingJdtObject.getDeclaringType)
        case _ => false
      }

    }

}

