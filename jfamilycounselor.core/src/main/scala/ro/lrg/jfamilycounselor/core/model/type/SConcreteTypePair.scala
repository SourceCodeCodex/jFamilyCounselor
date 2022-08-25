package ro.lrg.jfamilycounselor.core.model.`type`

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.core.MConcreteTypePair

final case class SConcreteTypePair(_1: SType, _2: SType)
    extends MConcreteTypePair {
  override def toString: String = s"${_1.toString}: Type, ${_2.toString}: Type"

  override val jdtElement1: IType = _1.jdtElement
  override val jdtElement2: IType = _2.jdtElement
}
