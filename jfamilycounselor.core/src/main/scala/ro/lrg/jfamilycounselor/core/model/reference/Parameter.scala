package ro.lrg.jfamilycounselor.core.model.reference

import org.eclipse.jdt.core.{Flags, ILocalVariable, IMethod, Signature}
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.method.Method

final case class Parameter(underlyingJdtObject: ILocalVariable)
  extends MemberReferenceVariable {
  override type UnderlyingJdtObjectType = ILocalVariable

  lazy val declaringMethod: Method = Method(
    underlyingJdtObject.getDeclaringMember.asInstanceOf[IMethod]
  )

  override lazy val isRelevant: Boolean =
    super.isRelevant && !Flags.isStatic(
      declaringMethod.underlyingJdtObject.getFlags
    ) && Signature.getTypeSignatureKind(
      typeSignature
    ) != Signature.ARRAY_TYPE_SIGNATURE

  override lazy val typeSignature: String = underlyingJdtObject.getTypeSignature

  override lazy val declaringType: Type = Type(
    underlyingJdtObject.getDeclaringMember.getDeclaringType
  )

  override def toString: String =
    s"($declaringMethod) / ${underlyingJdtObject.getElementName}: Parameter"
}
