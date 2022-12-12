package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.expression

import org.eclipse.jdt.core.IType
import org.eclipse.jdt.core.dom.{Expression => JDTExpression, _}
import ro.lrg.jfamilycounselor.core.model.`type`.Type

case class Expression(underlyingJdtObject: JDTExpression) {
  lazy val `type`: Option[Type] = for {
    binding <- Option(underlyingJdtObject.resolveTypeBinding())
    underlyingJavaObject <- Option(binding.getJavaElement) if underlyingJavaObject.isInstanceOf[IType]
  } yield Type(underlyingJavaObject.asInstanceOf[IType])

  lazy val isLocalVar: Boolean = !isField && !isParameter

  lazy val isField: Boolean = underlyingJdtObject match {
    case e: SimpleName =>
      Option(e.resolveBinding()).filter(_.getKind == IBinding.VARIABLE).exists(_.asInstanceOf[IVariableBinding].isField)
    case _ => false
  }

  lazy val isParameter: Boolean = underlyingJdtObject match {
    case e: SimpleName =>
      Option(e.resolveBinding()).filter(_.getKind == IBinding.VARIABLE).exists(_.asInstanceOf[IVariableBinding].isParameter)
    case _ => false
  }
}
