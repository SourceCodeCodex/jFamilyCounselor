package ro.lrg.jfamilycounselor.alg

import ro.lrg.jfamilycounselor.impl.alg.NameBasedAlgorithm
import ro.lrg.jfamilycounselor.impl.pair.{SConcreteTypePair, SRefPair}
import ro.lrg.jfamilycounselor.impl.ref.SRef

private[jfamilycounselor] trait UsedConcreteTypePairsAlgorithm {
  private[jfamilycounselor] def compute[R <: SRef](refPair: SRefPair[R]): List[SConcreteTypePair]
}

object UsedConcreteTypePairsAlgorithm {
  val nameBasedAlgorithm: UsedConcreteTypePairsAlgorithm = NameBasedAlgorithm
}