package ro.lrg.jfamilycounselor.alg

import ro.lrg.jfamilycounselor.impl.alg.{AssignmentsBasedAlgorithm, NameBasedAlgorithm}
import ro.lrg.jfamilycounselor.impl.model.pair.{SConcreteTypePair, SRefPair}
import ro.lrg.jfamilycounselor.impl.model.ref.SRef

private[jfamilycounselor] trait UsedConcreteTypePairsAlgorithm {
  private[jfamilycounselor] def compute[R <: SRef](refPair: SRefPair[R]): List[SConcreteTypePair]
}

object UsedConcreteTypePairsAlgorithm {
  val nameBasedAlgorithm: UsedConcreteTypePairsAlgorithm = NameBasedAlgorithm
  val assignmentsBasedAlgorithm: UsedConcreteTypePairsAlgorithm = AssignmentsBasedAlgorithm
}