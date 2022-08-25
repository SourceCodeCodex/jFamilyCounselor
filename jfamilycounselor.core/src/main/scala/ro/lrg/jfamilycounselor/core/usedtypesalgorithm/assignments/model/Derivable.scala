package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model

trait Derivable[T] {
  def canBeDerived: Boolean
  def derive: List[T]
}
