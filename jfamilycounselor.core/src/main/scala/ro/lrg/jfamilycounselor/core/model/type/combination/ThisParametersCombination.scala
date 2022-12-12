package ro.lrg.jfamilycounselor.core.model.`type`.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.pair.{ReferenceVariablesPair, ThisParameterPair}

private[`type`] object ThisParametersCombination extends ReferenceVariablesCombinationStrategy {
  override def combine(`type`: Type): List[ReferenceVariablesPair] = for {
    thisReference <- List(`type`.`this`)
    parameters <- `type`.relevantParameters
  } yield ThisParameterPair(thisReference, parameters)
}
