package ro.lrg.jfamilycounselor.core.model.`type`

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{Flags, IType}
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.`type`.reference.combination.{ParametersCombination, ThisParametersCombination}
import ro.lrg.jfamilycounselor.core.model.reference.{Field, Parameter, This}
import ro.lrg.jfamilycounselor.core.model.references.pair.ReferenceVariablesPair

import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

final case class Type(underlyingJdtObject: IType) {
  def apertureCoverage(alg: UsedTypesEstimation): Double =
    relevantReferenceVariablesPairs.map(_.apertureCoverage(alg)).min

  def isRelevant: Boolean = {
    !underlyingJdtObject.isAnonymous &&
      underlyingJdtObject.getTypeParameters.isEmpty &&
      relevantReferenceVariablesPairs.nonEmpty
  }

  def relevantReferenceVariablesPairs: List[ReferenceVariablesPair] =
    ParametersCombination.combine(this) ++
      ThisParametersCombination.combine(this)

  def isLeafAndConcrete: Boolean = concreteCone.size == 1 && concreteCone.contains(this)

  def concreteCone: List[Type] = {
    val subtypes = underlyingJdtObject
      .newTypeHierarchy(new NullProgressMonitor())
      .getAllSubtypes(underlyingJdtObject)
      .toList

    val filtered = (underlyingJdtObject :: subtypes)
      .filterNot(t => t.isAnonymous)
      .filterNot(t => Flags.isInterface(t.getFlags))
      .filterNot(t => Flags.isAbstract(t.getFlags))
      .filterNot(t => Flags.isSynthetic(t.getFlags))

    filtered.map(Type)
  }

  private[`type`] val `this`: This = This(underlyingJdtObject)

  private[`type`] def relevantFields: List[Field] =
    underlyingJdtObject.getFields.toList.map(Field).par.filter(_.isRelevant).toList

  private[`type`] def relevantParameters: List[Parameter] =
    underlyingJdtObject.getMethods.toList
      .flatMap(_.getParameters.toList)
      .map(Parameter)
      .filter(_.isRelevant)

  override def toString: String = underlyingJdtObject.getFullyQualifiedName
}
