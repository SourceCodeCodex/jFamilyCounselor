package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair

import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.specific.one.ResolvedParameterDeriver
import ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignments.pair.specific.two.{ParameterParameterDeriver, ThisParameterDeriver}
import ro.lrg.jfamilycounselor.core.esimation.assignment.model.AssignmentsPair
import ro.lrg.jfamilycounselor.core.model.expression.{Expression, ParameterExpression, ThisExpression}

private object DeriverDispatcher {
  def derive(pair: AssignmentsPair): Either[AssignmentsPair, List[AssignmentsPair]] = {

    // This dispatching is used when the first assignment of the pair has a resolved type
    // We only derive the second assignment, the process being specific for each type of the
    // second assignment's expression
    def dispatchOne: Expression => Either[AssignmentsPair, List[AssignmentsPair]] = {
      case _: ParameterExpression => Right(ResolvedParameterDeriver.derive(pair))
      case _ => Left(pair)
    }

    // This dispatching is used in contrast with dispatchOne. This is used in order to reuse code.
    // We basically swap the assignments within the pair for the input and swap again all outputs.
    def dispatchOneSwapped: Expression => Either[AssignmentsPair, List[AssignmentsPair]] = {
      val swapped = pair.swap;
      {
        case _: ParameterExpression => Right(ResolvedParameterDeriver.derive(swapped).map(_.swap))
        case _ => Left(pair)
      }
    }

    // This dispatching is used when no assignment has a resolved type.
    // This may face future improvements, adding calling new derivers, when implemented.
    def dispatchTwo: (Expression, Expression) => Either[AssignmentsPair, List[AssignmentsPair]] = {
      case (_: ParameterExpression, _: ParameterExpression) => Right(ParameterParameterDeriver.derive(pair))
      case (_: ThisExpression, _: ParameterExpression) => Right(ThisParameterDeriver.derive(pair))
      case (_: ParameterExpression, _: ThisExpression) => Right(ThisParameterDeriver.derive(pair.swap).map(_.swap))
      case _ => Left(pair)
    }


    pair match {
      case _ if pair._1.resolveConcreteType.isDefined => dispatchOne(pair._2.expression)
      case _ if pair._2.resolveConcreteType.isDefined => dispatchOneSwapped(pair._1.expression)
      case _ => dispatchTwo(pair._1.expression, pair._2.expression)
    }
  }
}
