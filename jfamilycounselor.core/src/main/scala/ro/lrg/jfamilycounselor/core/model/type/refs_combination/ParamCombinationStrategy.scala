package ro.lrg.jfamilycounselor.core.model.`type`.refs_combination

import ro.lrg.jfamilycounselor.core.model.`type`.SType
import ro.lrg.jfamilycounselor.core.model.ref.{SParam, SParamPair, SRefPair}

object ParamCombinationStrategy extends RefCombinationStrategy[SParam] {

  override def combine(sType: SType): List[SRefPair[SParam]] = {
    val susParams = sType.jdtElement.getMethods
      .toList
      .flatMap(_.getParameters.toList)
      .map(SParam)
      .filter(_.isSusceptible)

    val validCombinations: List[(SParam, SParam)] =
      comb2(susParams)
        .filterNot { case (sp1, sp2) =>
          sp1.declaringMethod.jdtElement.isConstructor && !sp2.declaringMethod.jdtElement.isConstructor
        }
        .filterNot { case (sp1, sp2) =>
          !sp1.declaringMethod.jdtElement.isConstructor && sp2.declaringMethod.jdtElement.isConstructor
        }
        .filterNot { case (sp1, sp2) =>
          sp1.declaringMethod.jdtElement.isConstructor && sp2.declaringMethod.jdtElement.isConstructor &&
            sp1.declaringMethod != sp2.declaringMethod
        }
        .filterNot { case (sp1, sp2) =>
          sp1.declaredType0 == sp2.declaredType0
        }

    validCombinations.map(p => new SParamPair(p._1, p._2))
  }
}
