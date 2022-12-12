package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignments_pair

import ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignment.Assignment
import ro.lrg.jfamilycounselor.core.model.`type`.pair.TypesPair

private object AssignmentsPairDeriver {

  def derive(pair: AssignmentsPair): DerivationResult = {
    val _1 = pair._1
    val _2 = pair._2

    if (_1.resolvedConcreteType.isDefined && _2.resolvedConcreteType.isDefined)
      ResolvedConcreteTypePair(TypesPair(_1.resolvedConcreteType.get, _2.resolvedConcreteType.get))
    else if (!pair.canBeDerived)
      InconclusiveTypePair(TypesPair(_1.lastRecordedType, _2.lastRecordedType))
    else
      NewAssignmentsPairs(deriveNewAssignments(pair))
  }

  def deriveNewAssignments(pair: AssignmentsPair): List[AssignmentsPair] = {
    val _1 = pair._1
    val _2 = pair._2

    val newAssignmentsPairs = for {
      n1 <- _1.derivation
      n2 <- _2.derivation
    } yield AssignmentsPair(n1, n2, pair.depth)

    newAssignmentsPairs.flatMap(p =>
      if (p.canBeDerived)
        ExpressionDeriver
          .derive(p._1.expression, p._2.expression)
          .map(e => {
            val assignment1 =
              Assignment(p._1.referenceVariable, e._1, p._1.expression.`type`.get)
            val assignment2 =
              Assignment(p._2.referenceVariable, e._2, p._2.expression.`type`.get)
            AssignmentsPair(assignment1, assignment2, pair.depth + 1)
          })
      else
        List(p)
    )
  }

}
