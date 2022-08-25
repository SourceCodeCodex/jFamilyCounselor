package ro.lrg.jfamilycounselor.core.model.ref

import ro.lrg.jfamilycounselor.core.{MConcreteTypePair, MRefPair, UsedConcreteTypePairsAlgorithm}
import ro.lrg.jfamilycounselor.core.model.`type`.SConcreteTypePair

import scala.jdk.CollectionConverters._

sealed abstract case class SRefPair[R <: SRef](_1: R, _2: R) extends MRefPair {

  override type jdtRefType = R#jdtType
  override val jdtElement1: jdtRefType = _1.jdtElement
  override val jdtElement2: jdtRefType = _2.jdtElement

  override def aperture: Int = possibleConcreteTypePairs.size

  override def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double =
    usedConcreteTypePairs(alg).size * 1.0 / possibleConcreteTypePairs.size

  override def possibleConcreteTypePairs: java.util.List[MConcreteTypePair] =
    (possibleConcreteTypePairs0: List[MConcreteTypePair]).asJava

  override def usedConcreteTypePairs(
      alg: UsedConcreteTypePairsAlgorithm
  ): java.util.List[MConcreteTypePair] =
    alg.compute(this).asJava

  def possibleConcreteTypePairs0: List[SConcreteTypePair] = for {
    st1 <- _1.declaredType.concreteCone
    st2 <- _2.declaredType.concreteCone
  } yield SConcreteTypePair(st1, st2)
}

final class SFieldPair(_1: SField, _2: SField)
    extends SRefPair[SField](_1, _2) {
  override def toString: String =
    s"${_1.toString}: Field, ${_2.toString}: Field"
}

final class SParamPair(_1: SParam, _2: SParam)
    extends SRefPair[SParam](_1, _2) {
  override def toString: String =
    s"${_1.toString}: Param, ${_2.toString}: Param"
}
