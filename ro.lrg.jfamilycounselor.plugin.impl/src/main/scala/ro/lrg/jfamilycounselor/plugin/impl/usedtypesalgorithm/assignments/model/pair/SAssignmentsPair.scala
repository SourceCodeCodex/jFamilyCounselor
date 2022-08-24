package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.pair

import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.{SConcreteTypePair, STypePair}
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.Derivable
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.assgn.SAssignment

case class SAssignmentsPair(_1: SAssignment, _2: SAssignment, depth: Int)
    extends Derivable[SAssignmentsPair] {

  //format: off
  private lazy val canConcreteTypesPairBeResolved: Boolean =
    _1.sExpression.canConcreteTypeBeResolved && _2.sExpression.canConcreteTypeBeResolved

  private lazy val resolvedConcreteTypePair: ResolvedConcreteTypePair =
    ResolvedConcreteTypePair(SConcreteTypePair(_1.sExpression.getType.get, _2.sExpression.getType.get))

  private lazy val inconclusiveTypePair: InconclusiveTypePair =
    InconclusiveTypePair(STypePair(_1.mostConcreteType, _2.mostConcreteType))

  override def canBeDerived: Boolean = {
    _1.sExpression.getType.isDefined && (_1.sExpression.isSimpleExp || _1.sExpression.isComplexExp) &&
    _2.sExpression.getType.isDefined && (_2.sExpression.isSimpleExp || _2.sExpression.isComplexExp)
  }
  //format: on

  lazy val derive: List[SAssignmentsPair] = {
    val newAssignmentsPairs = for {
      n1 <- _1.derive
      n2 <- _2.derive
    } yield SAssignmentsPair(n1, n2, depth)

    newAssignmentsPairs.flatMap(p =>
      if (p.canBeDerived)
        ExpressionsDerivation
          .derive(p._1.sExpression, p._2.sExpression)
          .map(exps => {
            val assignment1 =
              SAssignment(p._1.sRef, exps._1, p._1.sExpression.getType.get)
            val assignment2 =
              SAssignment(p._2.sRef, exps._2, p._2.sExpression.getType.get)
            SAssignmentsPair(assignment1, assignment2, depth + 1)
          })
      else
        List(p)
    )
  }

  def derivationResult: AssignmentsPairDerivationResult =
    if (canConcreteTypesPairBeResolved)
      resolvedConcreteTypePair
    else if (!canBeDerived)
      inconclusiveTypePair
    else
      NewAssignmentsPairs(derive)

}
