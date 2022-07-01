package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model

trait Derivable[T] {
  def canBeDerived: Boolean
  def derive: List[T]
}
