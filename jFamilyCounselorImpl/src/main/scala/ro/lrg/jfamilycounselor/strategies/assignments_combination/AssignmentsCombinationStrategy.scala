package ro.lrg.jfamilycounselor.strategies.assignments_combination

import ro.lrg.jfamilycounselor.model.assignment.SAssignmentPair
import ro.lrg.jfamilycounselor.model.ref.{SRef, SRefPair}

trait AssignmentsCombinationStrategy[R <: SRef] {
  def combine(refPair: SRefPair[R]): List[SAssignmentPair]
}
