package ro.lrg.jfamilycounselor.core.model.expression

import org.eclipse.jdt.core.IField
import org.eclipse.jdt.core.dom.{FieldAccess, IBinding, IVariableBinding, SimpleName, SuperFieldAccess, Expression => JDTExpression}

private object ExpressionTypeUtil {

  def jdtType[T](e: JDTExpression): Option[T] = Option(e.resolveTypeBinding()).flatMap(b => Option(b.getJavaElement.asInstanceOf[T]))

  def jdtElement[T](e: SimpleName): Option[T] = Option(e.resolveBinding()).flatMap(b => Option(b.getJavaElement.asInstanceOf[T]))

  def jdtElement(e: FieldAccess): Option[IField] = Option(e.resolveFieldBinding()).flatMap(b => Option(b.getJavaElement.asInstanceOf[IField]))

  def jdtElement(e: SuperFieldAccess): Option[IField] = Option(e.resolveFieldBinding()).flatMap(b => Option(b.getJavaElement.asInstanceOf[IField]))

  def isLocalVariable(e: SimpleName): Boolean = Option(e.resolveBinding()).exists(_.getKind == IBinding.VARIABLE) && !isField(e) && !isParameter(e)

  def isField(e: SimpleName): Boolean = e match {
    case e: SimpleName =>
      Option(e.resolveBinding()).filter(_.getKind == IBinding.VARIABLE).exists(_.asInstanceOf[IVariableBinding].isField)
    case _ => false
  }

  def isParameter(e: SimpleName): Boolean = e match {
    case e: SimpleName =>
      Option(e.resolveBinding()).filter(_.getKind == IBinding.VARIABLE).exists(_.asInstanceOf[IVariableBinding].isParameter)
    case _ => false
  }
}
