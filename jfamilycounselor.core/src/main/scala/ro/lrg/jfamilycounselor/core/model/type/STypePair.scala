package ro.lrg.jfamilycounselor.core.model.`type`

final case class STypePair(_1: SType, _2: SType) {
  def allConcreteCombinations: List[SConcreteTypePair] = for {
    st1 <- _1.concreteCone
    st2 <- _2.concreteCone
  } yield SConcreteTypePair(st1, st2)
}
