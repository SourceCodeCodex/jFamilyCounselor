package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.references.pair.{FieldFieldPair, ReferenceVariablesPair}

private[`type`] object FieldsCombination extends ReferenceVariablesCombinationStrategy[FieldFieldPair] {
  override def combine(`type`: Type): List[FieldFieldPair] =
    combinationsOfTwo(`type`.relevantFields).map(p => FieldFieldPair(p._1, p._2))
}