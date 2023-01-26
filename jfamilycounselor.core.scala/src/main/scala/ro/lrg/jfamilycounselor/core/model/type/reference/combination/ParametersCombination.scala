package ro.lrg.jfamilycounselor.core.model.`type`.reference.combination

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.Parameter
import ro.lrg.jfamilycounselor.core.model.references.pair.ParameterParameterPair

private[`type`] object ParametersCombination extends ReferenceVariablesCombinationStrategy[ParameterParameterPair] {

  override def combine(`type`: Type): List[ParameterParameterPair] = {
    val validCombinations: List[(Parameter, Parameter)] =
      combinationsOfTwo(`type`.parameters.filter(_.isRelevant))
        .filterNot { case (sp1, sp2) =>
          sp1.declaringMethod.underlyingJdtObject.isConstructor && !sp2.declaringMethod.underlyingJdtObject.isConstructor
        }
        .filterNot { case (sp1, sp2) =>
          !sp1.declaringMethod.underlyingJdtObject.isConstructor && sp2.declaringMethod.underlyingJdtObject.isConstructor
        }
        .filterNot { case (sp1, sp2) =>
          sp1.declaringMethod.underlyingJdtObject.isConstructor && sp2.declaringMethod.underlyingJdtObject.isConstructor &&
            sp1.declaringMethod != sp2.declaringMethod
        }
        .filterNot { case (sp1, sp2) =>
          sp1.`type` == sp2.`type`
        }

    validCombinations.map(p => ParameterParameterPair(p._1, p._2))
  }
}
