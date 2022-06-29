package ro.lrg.jfamilycounselor.impl.model.pair

import ro.lrg.jfamilycounselor.impl.model.ref.SParam

private[jfamilycounselor] final class SParamPair(
    override val _1: SParam,
    override val _2: SParam
) extends SRefPair[SParam](_1, _2) {
  override def toString: String =
    s"${_1.toString}: Param, ${_2.toString}: Param"

  override def equals(other: Any): Boolean = other match {
    case that: SParamPair =>
      super.equals(that) &&
        _1 == that._1 &&
        _2 == that._2
    case _ => false
  }
}
