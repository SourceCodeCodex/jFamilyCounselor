package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.specific.one

import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.expression.ParameterExpression

object ResolvedParameterDeriver {

  def derive(pair: AssignmentsPair): List[AssignmentsPair] = {
    val p = pair._2.expression.asInstanceOf[ParameterExpression]

    for {
      call <- p.referenceVariable.declaringMethod.calls
      (a1, a2) = (
        pair._1,
        pair._2.copy(expression = call.argumentAt(p.referenceVariable.declaringMethod.indexOf(p.referenceVariable)), lastRecordedType = p.referenceVariable.typeUnsafe)
      )
    } yield AssignmentsPair(a1, a2, pair.depth + 1, pair.combinations)

  }

}
