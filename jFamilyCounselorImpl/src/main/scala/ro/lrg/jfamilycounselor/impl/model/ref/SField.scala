package ro.lrg.jfamilycounselor.impl.model.ref

import org.eclipse.jdt.core.{Flags, IField, IType}

private[jfamilycounselor] final class SField(field: IField) extends SRef {

  override type jdtType = IField
  override val jdtElement: jdtType = field

  override lazy val isSusceptible: Boolean = super.isSusceptible &&
    !Flags.isStatic(field.getFlags)

  override protected def typeSignature: String = field.getTypeSignature

  override protected def declaringType: IType = field.getDeclaringType

  override def toString: String = field.getElementName
}
