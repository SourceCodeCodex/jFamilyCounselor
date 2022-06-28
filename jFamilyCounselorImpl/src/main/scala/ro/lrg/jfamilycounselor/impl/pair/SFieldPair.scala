package ro.lrg.jfamilycounselor.impl.pair

import org.eclipse.jdt.core.IField
import ro.lrg.jfamilycounselor.impl.ref.SField

private[jfamilycounselor] final class SFieldPair(
    override val _1: SField,
    override val _2: SField
) extends SRefPair[SField](_1, _2) {
  override def toString: String =
    s"${_1.toString}: Field, ${_2.toString}: Field"

  override def equals(other: Any): Boolean = other match {
    case that: SFieldPair =>
      super.equals(that) &&
        _1 == that._1 &&
        _2 == that._2
    case _ => false
  }
}
