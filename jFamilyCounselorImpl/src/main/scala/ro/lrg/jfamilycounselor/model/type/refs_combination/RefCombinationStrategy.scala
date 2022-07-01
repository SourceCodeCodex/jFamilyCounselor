package ro.lrg.jfamilycounselor.model.`type`.refs_combination

import ro.lrg.jfamilycounselor.model.`type`.SType
import ro.lrg.jfamilycounselor.model.ref.{SRef, SRefPair}

trait RefCombinationStrategy[R <: SRef] {
  protected def comb2(l: List[R]): List[(R, R)] = l
    .combinations(2)
    .flatMap {
      case List(_1, _2) => List((_1, _2))
      case _            => List()
    }
    .toList

  def combine(sType: SType): List[SRefPair[R]]
}
