package ro.lrg.jfamilycounselor.metamodel.scala

import org.eclipse.jdt.core.{IField, IJavaElement, ILocalVariable}
import ro.lrg.jfamilycounselor.model.ref.{SField, SFieldPair, SParam, SParamPair}

trait MRefPair {
  type jdtRefType <: IJavaElement
  def jdtElement1: jdtRefType
  def jdtElement2: jdtRefType

  def aperture: Int
  def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double

  def possibleConcreteTypePairs: java.util.List[MConcreteTypePair]
  def usedConcreteTypePairs(alg: UsedConcreteTypePairsAlgorithm): java.util.List[MConcreteTypePair]
}

object MRefPair {
  def asScala(r1: IField, r2: IField): MRefPair = {
    val sf1 = SField(r1)
    val sf2 = SField(r2)
    new SFieldPair(sf1, sf2)
  }

  def apply(r1: ILocalVariable, r2: ILocalVariable): MRefPair = {
    val sf1 = SParam(r1)
    val sf2 = SParam(r2)
    new SParamPair(sf1, sf2)
  }
}
