package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model

trait Derivable[T] {
  def canBeDerived: Boolean
  def derive: List[T]
}
