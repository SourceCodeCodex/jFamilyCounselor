package ro.lrg.jfamilycounselor

import org.eclipse.jdt.core.{IField, IJavaElement, ILocalVariable}
import ro.lrg.jfamilycounselor.alg.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.impl.pair.{SFieldPair, SParamPair}
import ro.lrg.jfamilycounselor.impl.ref.{SField, SParam}

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
  def apply(r1: IField, r2: IField): MRefPair = {
    val sf1 = new SField(r1)
    val sf2 = new SField(r2)
    new SFieldPair(sf1, sf2)
  }

  def apply(r1: ILocalVariable, r2: ILocalVariable): MRefPair = {
    val sf1 = new SParam(r1)
    val sf2 = new SParam(r2)
    new SParamPair(sf1, sf2)
  }
}
