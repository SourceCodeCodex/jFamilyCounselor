package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm

import ro.lrg.jfamilycounselor.plugin.impl.{MConcreteTypePair, MRefPair, UsedConcreteTypePairsAlgorithm}
import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.SConcreteTypePair
import ro.lrg.jfamilycounselor.plugin.impl.model.ref.{SRef, SRefPair}

trait UsedConcreteTypePairsAlgorithm0 extends UsedConcreteTypePairsAlgorithm {
  override def compute(refPair: MRefPair): List[MConcreteTypePair] = compute0(
    refPair.asInstanceOf[SRefPair[_ <: SRef]]
  )

  protected def compute0(refPair: SRefPair[_ <: SRef]): List[SConcreteTypePair]
}
