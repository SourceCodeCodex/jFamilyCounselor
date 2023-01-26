package ro.lrg.jfamilycounselor.core.model.reference

import org.eclipse.jdt.core._
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.method.Method

/** There are two types of reference variables:
  *   - One that is specifically declared within a class (being a member of that class),
  *     such as fields and parameters.
  *   - "this" is also a reference, pointing to the current object.
  *     There two types are unified by this trait in order to provide uniform handling.
  */
sealed trait ReferenceVariable {
  type UnderlyingJdtObjectType <: IJavaElement

  /** Represents the underlying JDT object that describes the reference
    * (IField for fields, ILocalVariable for parameters).
    * We consider that the "this" reference is described by IType objects.
    */
  def underlyingJdtObject: UnderlyingJdtObjectType

  /** Represents the type of the reference in our model. It is in fact an
    * optional value, as, in some cases, the underlying JDT type of the reference
    * cannot be determined.
    */
  def `type`: Option[Type]

  /** Theoretically, the system will work only with relevant pairs, and therefore
    * the type can be resolved most of the times. This is used as a convenient hack.
    * To be used with care!
    */
  lazy val typeUnsafe: Type = `type`.get
}

final case class This(underlyingJdtObject: IType) extends ReferenceVariable {
  override type UnderlyingJdtObjectType = IType

  def isRelevant: Boolean = `type`.exists(t => !t.isObjectType && t.concreteCone.nonEmpty)

  override lazy val `type`: Option[Type] = Some(Type(underlyingJdtObject))

  override def toString: String = s"this: ${underlyingJdtObject.getElementName}"
}

final case class Field(underlyingJdtObject: IField) extends MemberReferenceVariable(underlyingJdtObject) with ReferenceVariable {
  override type UnderlyingJdtObjectType = IField

  override lazy val isRelevant: Boolean = super.isRelevant && !Flags.isStatic(underlyingJdtObject.getFlags)

  override def declaringType: Type = Type(underlyingJdtObject.getDeclaringType)

  override protected def typeSignature: String = underlyingJdtObject.getTypeSignature

  override def toString: String = underlyingJdtObject.getElementName
}

abstract class MethodVariableReference(underlyingJdtObject: ILocalVariable) extends MemberReferenceVariable(underlyingJdtObject) with ReferenceVariable {
  override type UnderlyingJdtObjectType = ILocalVariable

  lazy val declaringMethod: Method = Method(underlyingJdtObject.getDeclaringMember.asInstanceOf[IMethod])

  override lazy val isRelevant: Boolean =
    super.isRelevant && !Flags.isStatic(
      declaringMethod.underlyingJdtObject.getFlags
    ) && Signature.getTypeSignatureKind(
      typeSignature
    ) != Signature.ARRAY_TYPE_SIGNATURE

  override def typeSignature: String = underlyingJdtObject.getTypeSignature

  override def declaringType: Type = Type(underlyingJdtObject.getDeclaringMember.getDeclaringType)

  override def toString: String = s"$declaringMethod / ${underlyingJdtObject.getElementName}"
}

final case class Parameter(underlyingJdtObject: ILocalVariable) extends MethodVariableReference(underlyingJdtObject)

final case class LocalVariable(underlyingJdtObject: ILocalVariable) extends MethodVariableReference(underlyingJdtObject)