package ro.lrg.jfamilycounselor.core.model.reference

import org.eclipse.jdt.core.{Flags, IField}
import ro.lrg.jfamilycounselor.core.model.`type`.Type

final case class Field(underlyingJdtObject: IField)
  extends MemberReferenceVariable {
  override type UnderlyingJdtObjectType = IField

  override lazy val isRelevant: Boolean =
    super.isRelevant && !Flags.isStatic(underlyingJdtObject.getFlags)
  override lazy val declaringType: Type = Type(
    underlyingJdtObject.getDeclaringType
  )
  override protected lazy val typeSignature: String =
    underlyingJdtObject.getTypeSignature

  override def toString: String = underlyingJdtObject.getElementName + ": Field"
}
