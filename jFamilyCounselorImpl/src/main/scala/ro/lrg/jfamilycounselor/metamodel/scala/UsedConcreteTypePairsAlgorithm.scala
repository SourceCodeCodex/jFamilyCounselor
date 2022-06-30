package ro.lrg.jfamilycounselor.metamodel.scala

import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.AssignmentsBasedAlgorithm
import ro.lrg.jfamilycounselor.used_types_algorithm.name.NameBasedAlgorithm

trait UsedConcreteTypePairsAlgorithm {
  def compute(refPair: MRefPair): List[MConcreteTypePair]
}

object UsedConcreteTypePairsAlgorithm {
  val nameBasedAlgorithm: UsedConcreteTypePairsAlgorithm = NameBasedAlgorithm
  val assignmentsBasedAlgorithm: UsedConcreteTypePairsAlgorithm = AssignmentsBasedAlgorithm
}