package ro.lrg.jfamilycounselor

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.alg.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.impl.model.`type`.SType

trait MType {
  def jdtElement: IType

  def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double

  def susceptibleRefPairs: java.util.List[MRefPair]

}

object MType {
  def apply(`type`: IType):  MType = new SType(`type`)
}
