package ro.lrg.jfamilycounselor.impl.model.pair

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.MConcreteTypePair
import ro.lrg.jfamilycounselor.impl.model.`type`.SType

private[jfamilycounselor] final class SConcreteTypePair(
    val _1: SType,
    val _2: SType
) extends MConcreteTypePair {
  override def toString: String = s"${_1.toString}: Type, ${_2.toString}: Type"

  override val jdtElement1: IType = _1.jdtElement
  override val jdtElement2: IType = _2.jdtElement

  override def equals(other: Any): Boolean = other match {
    case that: SConcreteTypePair =>
      _1 == that._1 &&
        _2 == that._2
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(_1, _2)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
