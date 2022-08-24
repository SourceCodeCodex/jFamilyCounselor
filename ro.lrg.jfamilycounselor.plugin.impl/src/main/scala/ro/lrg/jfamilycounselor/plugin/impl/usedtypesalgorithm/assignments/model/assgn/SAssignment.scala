package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.assgn

import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.SType
import ro.lrg.jfamilycounselor.plugin.impl.model.ref.SRef
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.{Derivable, SExpression}

case class SAssignment(
    sRef: SRef,
    sExpression: SExpression,
    mostConcreteType: SType
) extends Derivable[SAssignment] {
  def canBeDerived: Boolean =
    sExpression.getType.isDefined && sExpression.isSimpleExp

  def derive: List[SAssignment] = {
    def internal(
        sAssignment: SAssignment,
        alreadyDerived: List[SExpression]
    ): List[SAssignment] = {
      if (!sAssignment.canBeDerived)
        List(sAssignment)
      else
        ExpressionDerivation
          .derive(sAssignment.sExpression)
          .filterNot(alreadyDerived.contains)
          .map(sExp => SAssignment(sAssignment.sRef, sExp, sAssignment.sExpression.getType.get))
          .flatMap(newAssignment => internal(newAssignment, newAssignment.sExpression :: alreadyDerived))
    }

    internal(this, List())
  }

}
