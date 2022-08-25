package ro.lrg.jfamilycounselor.core

import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.AssignmentsBasedAlgorithm
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.name.NameBasedAlgorithm

trait UsedConcreteTypePairsAlgorithm {
  def compute(refPair: MRefPair): List[MConcreteTypePair]
}

object UsedConcreteTypePairsAlgorithm {
  val nameBasedAlgorithm: UsedConcreteTypePairsAlgorithm = NameBasedAlgorithm
  val assignmentsBasedAlgorithm: UsedConcreteTypePairsAlgorithm = AssignmentsBasedAlgorithm
}