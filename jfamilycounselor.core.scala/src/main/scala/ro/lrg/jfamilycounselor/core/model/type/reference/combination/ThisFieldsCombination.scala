package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.This
import ro.lrg.jfamilycounselor.core.model.references.pair.ThisFieldPair

private[`type`] object ThisFieldsCombination extends ReferenceVariablesCombinationStrategy[ThisFieldPair] {
  override def combine(`type`: Type): List[ThisFieldPair] = for {
    thisReference <- List(`type`.`this`) if `type`.`this`.isRelevant
    field <- `type`.fields.filter(_.isRelevant).filter(_.typeUnsafe != `type`)
  } yield ThisFieldPair(thisReference, field)
}
