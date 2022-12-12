package ro.lrg.jfamilycounselor.core.model.reference

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.core.model.`type`.Type

final case class This(underlyingJdtObject: IType) extends ReferenceVariable {
  override type UnderlyingJdtObjectType = IType

  override def `type`: Option[Type] = Some(Type(underlyingJdtObject))

  override def toString: String = s"${underlyingJdtObject.getElementName}: This"
}
