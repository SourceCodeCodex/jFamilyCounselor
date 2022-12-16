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

  lazy val isRelevant: Boolean = {
      !underlyingJdtObject.isAnonymous &&
        underlyingJdtObject.getTypeParameters.isEmpty &&
        // Should test relevantReferenceVariablesPairs.nonEmpty for correctness
        // This represents an optimisation. When analysing fields, please update this.
        parameters.par.exists(_.isRelevant)
  }

  lazy val relevantReferenceVariablesPairs: List[ReferenceVariablesPair] =
    ParametersCombination.combine(this) ++
      ThisParametersCombination.combine(this)

  lazy val isLeafAndConcrete: Boolean = concreteCone.size == 1 && concreteCone.contains(this)

  lazy val concreteCone: List[Type] = {
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

  private[`type`] lazy val `this`: This = This(underlyingJdtObject)

  private[`type`] lazy val fields: List[Field] = underlyingJdtObject.getFields.toList.map(Field)

  private[`type`] lazy val relevantFields: List[Field] = fields.par.filter(_.isRelevant).toList

  private[`type`] lazy val parameters = underlyingJdtObject.getMethods.toList.flatMap(_.getParameters.toList).map(Parameter)

  private[`type`] lazy val relevantParameters: List[Parameter] = parameters.par.filter(_.isRelevant).toList

  override lazy val toString: String = underlyingJdtObject.getFullyQualifiedName
}
