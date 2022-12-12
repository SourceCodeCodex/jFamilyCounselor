package ro.lrg.jfamilycounselor.core.model.reference

import org.eclipse.jdt.core._
import ro.lrg.jfamilycounselor.core.model.`type`.Type

/** There are two types of reference variables:
  *   - One that is specifically declared within a class (being a member of that class),
  *     such as fields and parameters.
  *   - "this" is also a reference, pointing to the current object.
  *     There two types are unified by this trait in order to provide uniform handling.
  */
abstract class ReferenceVariable {
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
