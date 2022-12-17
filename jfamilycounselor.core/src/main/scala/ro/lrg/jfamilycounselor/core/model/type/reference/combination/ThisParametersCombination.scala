package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.This
import ro.lrg.jfamilycounselor.core.model.references.pair.{ReferenceVariablesPair, ThisParameterPair}

private[`type`] object ThisParametersCombination extends ReferenceVariablesCombinationStrategy[ThisParameterPair] {
  override def combine(`type`: Type): List[ThisParameterPair] = for {
    thisReference <- List(This(`type`.underlyingJdtObject))
    parameters <- `type`.parameters.filter(_.isRelevant)
  } yield ThisParameterPair(thisReference, parameters)
}
