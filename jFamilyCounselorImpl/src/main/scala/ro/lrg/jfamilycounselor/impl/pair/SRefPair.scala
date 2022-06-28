package ro.lrg.jfamilycounselor.impl.pair

import ro.lrg.jfamilycounselor.impl.ref.SRef
import ro.lrg.jfamilycounselor.{
  MConcreteTypePair,
  MRefPair,
  UsedConcreteTypePairsAlgorithm
}

import scala.jdk.CollectionConverters._

private[jfamilycounselor] abstract class SRefPair[R <: SRef](
    val _1: R,
    val _2: R
) extends MRefPair {

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
    (usedConcreteTypePairs0(alg): List[MConcreteTypePair]).asJava

  def possibleConcreteTypePairs0: List[SConcreteTypePair] = for {
    st1 <- _1.declaredType.concreteCone
    st2 <- _2.declaredType.concreteCone
  } yield new SConcreteTypePair(st1, st2)

  def usedConcreteTypePairs0(
      alg: UsedConcreteTypePairsAlgorithm
  ): List[SConcreteTypePair] = alg.compute(this)

  def canEqual(other: Any): Boolean = other.isInstanceOf[SRefPair[_]]

  override def equals(other: Any): Boolean = other match {
    case that: SRefPair[_] =>
      (that canEqual this) &&
        _1 == that._1 &&
        _2 == that._2
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(_1, _2)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
