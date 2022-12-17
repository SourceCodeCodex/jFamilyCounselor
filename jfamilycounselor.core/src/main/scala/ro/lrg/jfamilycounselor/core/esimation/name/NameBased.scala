package ro.lrg.jfamilycounselor.core.esimation.name

import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair
import ro.lrg.jfamilycounselor.core.util.cache.Cache

import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

object NameBased extends UsedTypesEstimation {
  private val cache = Cache[(Type, Type), List[TypesPair]](2048)

  private implicit class TypeOps(`type`: Type) {
    private val tokensR =
      "(?<!(^|\\d))(?=\\d)|(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|_"

    def isCorrelatedByNameWith(otherType: Type): Boolean = {
      val tokens1 = `type`.underlyingJdtObject.getElementName.split(tokensR)
      val tokens2 = otherType.underlyingJdtObject.getElementName.split(tokensR)

      val commonTokensCount = tokens1.intersect(tokens2).length
      val avgTokenLength = (tokens1.length + tokens2.length) / 2.0

      (commonTokensCount / avgTokenLength) >= 0.5
    }
  }

  override def compute(referenceVariablesPair: ReferenceVariablesPair): List[TypesPair] = {
    if (cache.containsKey((referenceVariablesPair._1.typeUnsafe, referenceVariablesPair._2.typeUnsafe)))
      cache.get((referenceVariablesPair._1.typeUnsafe, referenceVariablesPair._2.typeUnsafe))
    else {

      val possibleTypePairs =
        referenceVariablesPair.possibleTypes

      val correlatedPairs =
        possibleTypePairs.par.filter(p => p._1.isCorrelatedByNameWith(p._2))

      val notCorrelated1 =
        referenceVariablesPair._1.typeUnsafe.concreteCone.filterNot(t =>
          correlatedPairs.exists(p => p._1 == t)
      )
      val notCorrelated2 =
        referenceVariablesPair._2.typeUnsafe.concreteCone.filterNot(t =>
          correlatedPairs.exists(p => p._2 == t)
        )

      val autoCorrelated = for {
        t1 <- notCorrelated1
        t2 <- notCorrelated2
      } yield TypesPair(t1, t2)

      val r = (correlatedPairs ++ autoCorrelated).toList
      cache.put((referenceVariablesPair._1.typeUnsafe, referenceVariablesPair._2.typeUnsafe), r)

      r
    }
  }

  override def toString: String = "NameBased"

}
