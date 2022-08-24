package ro.lrg.jfamilycounselor.plugin.impl.model.`type`.refs_combination

import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.SType
import ro.lrg.jfamilycounselor.plugin.impl.model.ref.{SField, SFieldPair, SParam, SRefPair}

object FieldCombinationStrategy extends RefCombinationStrategy[SField] {
  override def combine(sType: SType): List[SRefPair[SField]] = {
    val susFields = sType.jdtElement.getFields.toList
      .map(SField)
      .filter(_.isSusceptible)

    comb2(susFields).map(p => new SFieldPair(p._1, p._2))
  }
}
