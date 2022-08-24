package ro.lrg.jfamilycounselor.plugin.impl

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.SType

trait MType {
  def jdtElement: IType

  def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double

  def susceptibleRefPairs: java.util.List[MRefPair]
}

object MType {
  def asScala(`type`: IType):  MType =  SType(`type`)
}
