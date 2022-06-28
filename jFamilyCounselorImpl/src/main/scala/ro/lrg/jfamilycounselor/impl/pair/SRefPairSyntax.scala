package ro.lrg.jfamilycounselor.impl.pair

import ro.lrg.jfamilycounselor.impl.SType
import ro.lrg.jfamilycounselor.impl.ref.{SField, SParam}

object SRefPairSyntax {
  private def comb2[A](l: List[A]): List[(A, A)] = l
    .combinations(2)
    .flatMap {
      case List(_1, _2) => List((_1, _2))
      case _            => List()
    }
    .toList

  implicit class MakePairsSyntaxOps(sType: SType) {
    def fieldPairs: List[SFieldPair] = {
      val susFields = sType.jdtElement.getFields.toList
        .map(new SField(_))
        .filter(_.isSusceptible)

      comb2(susFields).map(p => new SFieldPair(p._1, p._2))
    }

    def paramPairs: List[SParamPair] = {
      val susFields = sType.jdtElement.getMethods.toList
        .flatMap(_.getParameters.toList)
        .map(new SParam(_))
        .filter(_.isSusceptible)

      comb2(susFields).map(p => new SParamPair(p._1, p._2))
    }

  }
}
