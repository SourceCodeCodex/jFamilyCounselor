package ro.lrg.jfamilycounselor.used_types_algorithm

import ro.lrg.jfamilycounselor.metamodel.scala.{
  MConcreteTypePair,
  MRefPair,
  UsedConcreteTypePairsAlgorithm
}
import ro.lrg.jfamilycounselor.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.model.ref.{SRef, SRefPair}

trait UsedConcreteTypePairsAlgorithm0 extends UsedConcreteTypePairsAlgorithm {
  override def compute(refPair: MRefPair): List[MConcreteTypePair] = compute0(
    refPair.asInstanceOf[SRefPair[_ <: SRef]]
  )

  protected def compute0(refPair: SRefPair[_ <: SRef]): List[SConcreteTypePair]
}
