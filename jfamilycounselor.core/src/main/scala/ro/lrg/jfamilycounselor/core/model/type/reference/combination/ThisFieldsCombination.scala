package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.This
import ro.lrg.jfamilycounselor.core.model.references.pair.ThisFieldPair

private[`type`] object ThisFieldsCombination extends ReferenceVariablesCombinationStrategy[ThisFieldPair] {
  override def combine(`type`: Type): List[ThisFieldPair] = for {
    thisReference <- List(This(`type`.underlyingJdtObject))
    field <- `type`.fields.filter(_.isRelevant)
  } yield ThisFieldPair(thisReference, field)
}
