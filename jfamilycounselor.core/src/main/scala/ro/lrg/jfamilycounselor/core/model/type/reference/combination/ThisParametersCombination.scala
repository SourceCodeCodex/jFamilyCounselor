package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.references.pair.{ReferenceVariablesPair, ThisParameterPair}

private[`type`] object ThisParametersCombination extends ReferenceVariablesCombinationStrategy[ThisParameterPair] {
  override def combine(`type`: Type): List[ThisParameterPair] = for {
    thisReference <- List(`type`.`this`)
    parameters <- `type`.relevantParameters
  } yield ThisParameterPair(thisReference, parameters)
}
