package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.name

import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.{SConcreteTypePair, SType}
import ro.lrg.jfamilycounselor.plugin.impl.model.ref.{SRef, SRefPair}
import ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.UsedConcreteTypePairsAlgorithm0

object NameBasedAlgorithm extends UsedConcreteTypePairsAlgorithm0 {
  private implicit class STypeOps(sType: SType) {
    private val tokensR =
      "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_"
    def isCorrelatedByNameWith(otherSType: SType): Boolean = {
      val tokens1 = sType.jdtElement.getElementName.split(tokensR)
      val tokens2 = otherSType.jdtElement.getElementName.split(tokensR)

      val commonTokensCount = tokens1.intersect(tokens2).length
      val avgTokenLength = (tokens1.length + tokens2.length) / 2.0

      (commonTokensCount / avgTokenLength) >= 0.5
    }
  }

  override protected def compute0(
      refPair: SRefPair[_ <: SRef]
  ): List[SConcreteTypePair] = {

    val possibleTypePairs: List[SConcreteTypePair] =
      refPair.possibleConcreteTypePairs0

    val correlatedPairs: List[SConcreteTypePair] =
      possibleTypePairs.filter(p => p._1.isCorrelatedByNameWith(p._2))

    val notCorrelated1: List[SType] =
      refPair._1.declaredType.concreteCone.filterNot(t =>
        correlatedPairs.exists(p => p._1 == t)
      )
    val notCorrelated2: List[SType] =
      refPair._2.declaredType.concreteCone.filterNot(t =>
        correlatedPairs.exists(p => p._2 == t)
      )

    val autoCorrelated = for {
      t1 <- notCorrelated1
      t2 <- notCorrelated2
    } yield SConcreteTypePair(t1, t2)

    correlatedPairs ++ autoCorrelated
  }

}
