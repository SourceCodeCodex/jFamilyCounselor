package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.references.pair.{ReferenceVariablesPair, ThisFieldPair, ThisParameterPair}

private[`type`] object ThisFieldsCombination extends ReferenceVariablesCombinationStrategy[ThisFieldPair] {
  override def combine(`type`: Type): List[ThisFieldPair] = for {
    thisReference <- List(`type`.`this`)
    field <- `type`.relevantFields
  } yield ThisFieldPair(thisReference, field)
}
