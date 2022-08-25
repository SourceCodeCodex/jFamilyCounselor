package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model

import org.eclipse.jdt.core.IType
import org.eclipse.jdt.core.dom._
import ro.lrg.jfamilycounselor.core.model.`type`.SType

case class SExpression(expression: Expression) {

  lazy val getType: Option[SType] =
    Option.when(
      expression.resolveTypeBinding().getJavaElement.isInstanceOf[IType]
    )(
      SType(expression.resolveTypeBinding().getJavaElement.asInstanceOf[IType])
    )

  lazy val canConcreteTypeBeResolved: Boolean = {
    getType match {
      case Some(sType) =>
        val cCone = sType.concreteCone
        cCone.size == 1 && cCone.contains(sType)
      case None => false
    }
  }

  lazy val isLocalVar: Boolean = !isField && !isParameter

  lazy val isField: Boolean = expression match {
    case e: SimpleName if e.resolveBinding().getKind == IBinding.VARIABLE => e.resolveBinding().asInstanceOf[IVariableBinding].isField
    case _ => false
  }

  lazy val isParameter: Boolean = expression match {
    case e: SimpleName if e.resolveBinding().getKind == IBinding.VARIABLE => e.resolveBinding().asInstanceOf[IVariableBinding].isParameter
    case _ => false
  }

  lazy val isSimpleExp: Boolean =
    expression match {
      case _ if isLocalVar            => true
      case _: Assignment              => true
      case _: ConditionalExpression   => true
      case _: ParenthesizedExpression => true
      case _                          => false
    }

  lazy val isComplexExp: Boolean = {
    expression match {
      case _ if isParameter => true
      case _ => false
    }
  }

}
