package ro.lrg.jfamilycounselor.core.usedtypesalgorithm

import ro.lrg.jfamilycounselor.core.{MConcreteTypePair, MRefPair, UsedConcreteTypePairsAlgorithm}
import ro.lrg.jfamilycounselor.core.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.core.model.ref.{SRef, SRefPair}

trait UsedConcreteTypePairsAlgorithm0 extends UsedConcreteTypePairsAlgorithm {
  override def compute(refPair: MRefPair): List[MConcreteTypePair] = compute0(
    refPair.asInstanceOf[SRefPair[_ <: SRef]]
  )

  protected def compute0(refPair: SRefPair[_ <: SRef]): List[SConcreteTypePair]
}
