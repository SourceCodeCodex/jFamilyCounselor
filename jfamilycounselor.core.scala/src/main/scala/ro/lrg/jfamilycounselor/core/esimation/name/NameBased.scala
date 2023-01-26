package ro.lrg.jfamilycounselor.core.esimation.name

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair
import ro.lrg.jfamilycounselor.core.util.cache.Cache

import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

object NameBased extends UsedTypesEstimation {
  private val cache = Cache[(String, String), List[(IType, IType)]](128)

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
    val key1 = referenceVariablesPair._1.typeUnsafe.underlyingJdtObject.getFullyQualifiedName
    val key2 = referenceVariablesPair._2.typeUnsafe.underlyingJdtObject.getFullyQualifiedName
    val key = (key1, key2)

    if (cache.containsKey(key))
      cache.get(key).map { case (t1, t2) => TypesPair(Type(t1), Type(t2)) }
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
      cache.put(key, r.map(p => (p._1.underlyingJdtObject, p._2.underlyingJdtObject)))
      r
    }
  }

  override def toString: String = "NameBased"

}
