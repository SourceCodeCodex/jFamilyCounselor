package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair

import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignment.AssignmentsDeriver
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.{Assignment, AssignmentsPair}
import ro.lrg.jfamilycounselor.core.model.expression.{FieldExpression, ParameterExpression, ThisExpression}
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

object AssignmentsPairDeriver {
  def derive(pair: AssignmentsPair): DerivationResult = {
    val _1 = pair._1
    val _2 = pair._2

    // If concrete types can be resolved for both assignments
    // the derivation is complete
    if (_1.resolveConcreteType.isDefined && _2.resolveConcreteType.isDefined)
      ResolvedConcreteTypePair(
        TypesPair(_1.resolveConcreteType.get, _2.resolveConcreteType.get)
      )
    else if (!canBeDerived(pair)) {
      // If we do not know how to derive the current pair,
      // we mark the pair of last recorded types as inconclusive
      InconclusiveTypePair(TypesPair(_1.lastRecordedType, _2.lastRecordedType))
    } else {
      // Otherwise, the pair can be derived
      val (inconclusive, newAssignmentsPairs) = {
        val _1 = pair._1
        val _2 = pair._2

        // Initially, we get rid of simple expressions such as local variables, casts, etc.
        // from both assignments. Might that this might result in multiple assignments pairs as
        // as each assignment can generate multiple assignments through derivation (simple derivation).
        val pairs = for {
          n1 <- AssignmentsDeriver.derive(_1)
          n2 <- AssignmentsDeriver.derive(_2)
        } yield AssignmentsPair(n1, n2, pair.depth, pair.combinations)

        // Then, we try to derive more complex expressions, such as parameters, this, fields, etc.
        // The derivation is specific, depending on the types of the assigned expression
        // The dispatcher redirects each pair to the specific deriver.
        // If we do not know how to derive that assignments pair, we mark the last recorded
        // types of that assignment as inconclusive.
        val results = pairs.map(p => DeriverDispatcher.derive(p))
        val partitioned = results.partitionMap(identity)

        (partitioned._1, partitioned._2.flatten)
      }
      NewAssignmentsPairs(newAssignmentsPairs.toSet, inconclusive.toSet)
    }
  }

  // This will change whenever new derivers are implemented.
  def canBeDerived(pair: AssignmentsPair): Boolean = {
    def test(a: Assignment): Boolean =
      AssignmentsDeriver.canBeDerived(a) || (a.expression match {
        case _: ThisExpression | _: ParameterExpression => true
        case _: FieldExpression => false
        case _ => false
      })

    test(pair._1) || test(pair._2)
  }

}
