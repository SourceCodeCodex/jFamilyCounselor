package ro.lrg.jfamilycounselor.core.model.`type`.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.pair.{FieldFieldPair, ReferenceVariablesPair}

private[`type`] object FieldsCombination extends ReferenceVariablesCombinationStrategy {
  override def combine(`type`: Type): List[ReferenceVariablesPair] =
    combinationsOfTwo(`type`.relevantFields).map(p => FieldFieldPair(p._1, p._2))
}
