package ro.lrg.jfamilycounselor.core.model.`type`.pair

import ro.lrg.jfamilycounselor.core.model.`type`.Type

final case class TypesPair(_1: Type, _2: Type) {
  def concreteCombinations: List[TypesPair] = for {
    st1 <- _1.concreteCone
    st2 <- _2.concreteCone
  } yield TypesPair(st1, st2)

  override def toString: String = s"(${_1.toString}, ${_2.toString})"
}
