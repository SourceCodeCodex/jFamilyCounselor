package ro.lrg.jfamilycounselor.core

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.core.model.`type`.SType

trait MType {
  def jdtElement: IType

  def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double

  def mightHideFamilialCorrelationsRefPairs: java.util.List[MRefPair]
}

object MType {
  def asScala(`type`: IType):  MType =  SType(`type`)
}
