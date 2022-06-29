package ro.lrg.jfamilycounselor.impl.model.ref

import org.eclipse.jdt.core.{ILocalVariable, IMethod, IType}
import ro.lrg.jfamilycounselor.impl.model.method.SMethod

private[jfamilycounselor] final class SParam(param: ILocalVariable)
    extends SRef {

  override type jdtType = ILocalVariable
  override val jdtElement: jdtType = param

  def declaringMethod: SMethod = new SMethod(
    param.getDeclaringMember.asInstanceOf[IMethod]
  )

  override lazy val isSusceptible: Boolean = super.isSusceptible

  override protected def typeSignature: String = param.getTypeSignature

  override protected def declaringType: IType = param.getDeclaringMember.getDeclaringType

  override def toString: String = s"$declaringMethod / ${param.getElementName}"
}
