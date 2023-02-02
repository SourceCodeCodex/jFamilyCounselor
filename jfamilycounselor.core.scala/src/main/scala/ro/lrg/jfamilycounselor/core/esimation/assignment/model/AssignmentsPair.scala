package ro.lrg.jfamilycounselor.core.esimation.assignment.model

import ro.lrg.jfamilycounselor.core.model.expression._
import ro.lrg.jfamilycounselor.core.model.references.pair._

case class AssignmentsPair(_1: Assignment, _2: Assignment, depth: Int, combinations: Int) {
  lazy val swap: AssignmentsPair = copy(_1 = _2, _2 = _1)

  override def equals(obj: Any): Boolean = obj match {
    case p: AssignmentsPair => _1 == p._1 && _2 == p._2
    case _ => false
  }

  override def toString: String = s"(${_1}, ${_2}, $depth, $combinations)"
}

object AssignmentsPair {
  def selfAssigned(referenceVariablesPair: ReferenceVariablesPair): AssignmentsPair =
    referenceVariablesPair match {
      case ParameterParameterPair(_1, _2) => AssignmentsPair(
        Assignment(_1, ParameterExpression(_1), _1.typeUnsafe),
        Assignment(_2, ParameterExpression(_2), _2.typeUnsafe)
      )
      case FieldFieldPair(_1, _2) => AssignmentsPair(
        Assignment(_1, FieldExpression(_1), _1.typeUnsafe),
        Assignment(_2, FieldExpression(_2), _2.typeUnsafe)
      )
      case ThisParameterPair(_1, _2) => AssignmentsPair(
        Assignment(_1, ThisExpression(_1), _1.typeUnsafe),
        Assignment(_2, ParameterExpression(_2), _2.typeUnsafe)
      )
      case ThisFieldPair(_1, _2) => AssignmentsPair(
        Assignment(_1, ThisExpression(_1), _1.typeUnsafe),
        Assignment(_2, FieldExpression(_2), _2.typeUnsafe)
      )
    }

  def apply(_1: Assignment, _2: Assignment): AssignmentsPair = AssignmentsPair(_1, _2, 0, 0)
}