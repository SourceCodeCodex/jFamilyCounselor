package ro.lrg.jfamilycounselor.core.model.references.pair

import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.reference.{Field, Parameter, ReferenceVariable, This}
import ro.lrg.jfamilycounselor.core.model.types.pair.TypesPair

/**
  * Reasoning: Each type of pair needs to be handled differently. The framework behind (JDT)
  * makes it hard to find a generally applicable approach of uniformly handling references pairs
  * regardless of the pairs it contains in all scenarios. It is easier to exhaust all possibilities
  * use less abstract types when needed. (In particular, it is mostly about the combination phase
  * of the assignments-based estimation)
  *
  * Also, whenever a new pair of references enters the system, the algorithm needs to be somehow remodeled
  * (for assignments combination, we need a strategy that creates the assignments for that particular pair
  * of references). If we add a new type, we are forces to introduce logic that handles that type of pair.
  */
sealed abstract class ReferenceVariablesPair {
  type ReferenceVariableType1 <: ReferenceVariable
  type ReferenceVariableType2 <: ReferenceVariable

  def _1: ReferenceVariableType1

  def _2: ReferenceVariableType2

  def aperture: Int = possibleTypes.size

  def apertureCoverage(alg: UsedTypesEstimation): Double =
    usedTypes(alg).size * 1.0 / possibleTypes.size

  def possibleTypes: List[TypesPair] = for {
    st1 <- _1.typeUnsafe.concreteCone
    st2 <- _2.typeUnsafe.concreteCone
  } yield TypesPair(st1, st2)

  def usedTypes(estimation: UsedTypesEstimation): List[TypesPair] = estimation.compute(this)

  override def toString: String = s"${_1.toString}, ${_2.toString}"
}

case class ParameterParameterPair(_1: Parameter, _2: Parameter) extends ReferenceVariablesPair {
  override type ReferenceVariableType1 = Parameter
  override type ReferenceVariableType2 = Parameter
}

case class FieldFieldPair(_1: Field, _2: Field) extends ReferenceVariablesPair {
  override type ReferenceVariableType1 = Field
  override type ReferenceVariableType2 = Field
}

final case class ThisParameterPair(_1: This, _2: Parameter)
  extends ReferenceVariablesPair {
  override type ReferenceVariableType1 = This
  override type ReferenceVariableType2 = Parameter
}

final case class ThisFieldPair(_1: This, _2: Field)
  extends ReferenceVariablesPair {
  override type ReferenceVariableType1 = This
  override type ReferenceVariableType2 = Field
}
