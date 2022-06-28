package ro.lrg.jfamilycounselor.impl.alg
import ro.lrg.jfamilycounselor.impl.SType
import ro.lrg.jfamilycounselor.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.impl.pair.{SConcreteTypePair, SRefPair}
import ro.lrg.jfamilycounselor.impl.ref.SRef

private[jfamilycounselor] class NameBasedAlgorithm
    extends UsedConcreteTypePairsAlgorithm {
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

  private[jfamilycounselor] override def compute[R <: SRef](
      refPair: SRefPair[R]
  ): List[SConcreteTypePair] = {

    val possibleTypePairs: List[SConcreteTypePair] = refPair.possibleConcreteTypePairs0

    val correlatedPairs: List[SConcreteTypePair] =
      possibleTypePairs.filter(p => p._1.isCorrelatedByNameWith(p._2))

    val notCorrelated1: List[SType] = refPair._1.declaredType.concreteCone.filterNot(t =>
      correlatedPairs.exists(p => p._1 == t)
    )
    val notCorrelated2: List[SType] = refPair._2.declaredType.concreteCone.filterNot(t =>
      correlatedPairs.exists(p => p._2 == t)
    )

    val autoCorrelated = for {
      t1 <- notCorrelated1
      t2 <- notCorrelated2
    } yield new SConcreteTypePair(t1, t2)

    correlatedPairs ++ autoCorrelated
  }
}
