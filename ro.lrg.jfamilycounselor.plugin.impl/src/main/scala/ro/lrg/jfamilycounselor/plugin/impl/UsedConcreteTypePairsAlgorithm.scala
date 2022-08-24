package ro.lrg.jfamilycounselor.plugin.impl

import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.AssignmentsBasedAlgorithm
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.name.NameBasedAlgorithm

trait UsedConcreteTypePairsAlgorithm {
  def compute(refPair: MRefPair): List[MConcreteTypePair]
}

object UsedConcreteTypePairsAlgorithm {
  val nameBasedAlgorithm: UsedConcreteTypePairsAlgorithm = NameBasedAlgorithm
  val assignmentsBasedAlgorithm: UsedConcreteTypePairsAlgorithm = AssignmentsBasedAlgorithm
}