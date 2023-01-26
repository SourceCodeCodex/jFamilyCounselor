package ro.lrg.jfamilycounselor.core.model.`type`

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.`type`.reference.combination.{ParametersCombination, ThisParametersCombination}
import ro.lrg.jfamilycounselor.core.model.reference.{Field, Parameter, This}
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair
import ro.lrg.jfamilycounselor.core.util.hierarchy.ConcreteSubtypesResolver

final case class Type(underlyingJdtObject: IType) {
  def apertureCoverage(alg: UsedTypesEstimation): Double = {
    val discarded = Type(underlyingJdtObject)
    discarded.relevantReferenceVariablesPairs.map(_.apertureCoverage(alg)).min
  }

  def relevantReferenceVariablesPairs: List[ReferenceVariablesPair] = {
    val discarded = Type(underlyingJdtObject)
    ParametersCombination.combine(discarded) ++
      ThisParametersCombination.combine(discarded)
  }

  lazy val isRelevant: Boolean = {
    !underlyingJdtObject.isAnonymous &&
      underlyingJdtObject.getTypeParameters.isEmpty &&
      relevantReferenceVariablesPairs.nonEmpty
  }

  def concreteCone: List[Type] = ConcreteSubtypesResolver.concreteCone(underlyingJdtObject).map(Type)

  def isObjectType: Boolean = underlyingJdtObject.getFullyQualifiedName() == "java.lang.Object"

  def isLeafAndConcrete: Boolean = !isObjectType && concreteCone.size == 1 && concreteCone.contains(this)

  lazy val `this`: This = This(underlyingJdtObject)

  lazy val fields: List[Field] = underlyingJdtObject.getFields.toList.map(Field)

  lazy val parameters: List[Parameter] = underlyingJdtObject.getMethods.toList.flatMap(_.getParameters.toList).map(Parameter)

  override def toString: String = underlyingJdtObject.getFullyQualifiedName
}
