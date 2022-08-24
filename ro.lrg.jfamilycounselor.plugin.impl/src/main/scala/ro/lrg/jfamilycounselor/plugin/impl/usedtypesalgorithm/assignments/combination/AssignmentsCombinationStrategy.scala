package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.combination

import ro.lrg.jfamilycounselor.plugin.impl.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.pair.SAssignmentsPair

trait AssignmentsCombinationStrategy[R <: SRef] {
  def combine(refPair: SRefPair[R]): List[SAssignmentsPair]
}
