package ro.lrg.jfamilycounselor.strategies.refs_combination

import ro.lrg.jfamilycounselor.model.`type`.SType
import ro.lrg.jfamilycounselor.model.ref.{SField, SFieldPair, SParam, SRefPair}

object FieldCombinationStrategy extends RefCombinationStrategy[SField] {
  override def combine(sType: SType): List[SRefPair[SField]] = {
    val susFields = sType.jdtElement.getFields.toList
      .map(new SField(_))
      .filter(_.isSusceptible)

    comb2(susFields).map(p => new SFieldPair(p._1, p._2))
  }
}
