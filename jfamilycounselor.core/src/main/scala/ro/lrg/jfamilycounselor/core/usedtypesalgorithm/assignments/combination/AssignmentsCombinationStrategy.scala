package ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.combination

import ro.lrg.jfamilycounselor.core.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.core.usedtypesalgorithm.assignments.model.pair.SAssignmentsPair

trait AssignmentsCombinationStrategy[R <: SRef] {
  def combine(refPair: SRefPair[R]): List[SAssignmentsPair]
}
