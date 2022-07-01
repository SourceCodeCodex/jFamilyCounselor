package ro.lrg.jfamilycounselor.used_types_algorithm.assignments.combination

import ro.lrg.jfamilycounselor.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.used_types_algorithm.assignments.model.pair.SAssignmentsPair

trait AssignmentsCombinationStrategy[R <: SRef] {
  def combine(refPair: SRefPair[R]): List[SAssignmentsPair]
}
