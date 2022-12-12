package ro.lrg.jfamilycounselor.core.model.`type`.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.ReferenceVariable
import ro.lrg.jfamilycounselor.core.model.reference.pair.ReferenceVariablesPair

private[`type`] trait ReferenceVariablesCombinationStrategy {
  def combine(`type`: Type): List[ReferenceVariablesPair]

  protected def combinationsOfTwo[R <: ReferenceVariable](l: List[R]): List[(R, R)] = l
    .combinations(2)
    .flatMap {
      case List(_1, _2) => List((_1, _2))
      case _ => List()
    }
    .toList
}
