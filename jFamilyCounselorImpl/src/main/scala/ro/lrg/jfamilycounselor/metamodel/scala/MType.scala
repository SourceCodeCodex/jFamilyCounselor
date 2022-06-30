package ro.lrg.jfamilycounselor.metamodel.scala

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.model.`type`.SType

trait MType {
  def jdtElement: IType

  def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double

  def susceptibleRefPairs: java.util.List[MRefPair]
}

object MType {
  def asScala(`type`: IType):  MType =  SType(`type`)
}
