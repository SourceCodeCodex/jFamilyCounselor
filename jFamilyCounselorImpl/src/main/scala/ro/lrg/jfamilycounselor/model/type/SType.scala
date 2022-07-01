package ro.lrg.jfamilycounselor.model.`type`

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{Flags, IType}
import ro.lrg.jfamilycounselor.metamodel.scala.{
  MRefPair,
  MType,
  UsedConcreteTypePairsAlgorithm
}
import ro.lrg.jfamilycounselor.model.`type`.refs_combination.ParamCombinationStrategy
import ro.lrg.jfamilycounselor.model.ref.SRefPair

import scala.jdk.CollectionConverters._

final case class SType(jdtElement: IType) extends MType {

  override def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double =
    susceptibleRefPairs0.map(_.apertureCoverage(alg)).min

  override def susceptibleRefPairs: java.util.List[MRefPair] =
    (susceptibleRefPairs0: List[MRefPair]).asJava

  lazy val susceptibleRefPairs0: List[SRefPair[_]] = {
    ParamCombinationStrategy.combine(this)
  }

  lazy val canBeFamilyPolymorphismClient: Boolean = {
    !jdtElement.isAnonymous &&
    jdtElement.getTypeParameters.isEmpty &&
    susceptibleRefPairs0.nonEmpty
  }

  lazy val concreteCone: List[SType] = {
    import ro.lrg.jfamilycounselor.cache.implicits._

    def computeConcreteCone(tpe: IType): List[IType] = {
      val subtypes = tpe
        .newTypeHierarchy(new NullProgressMonitor())
        .getAllSubtypes(tpe)
        .toList

      (jdtElement :: subtypes)
        .filterNot(t => t.isAnonymous)
        .filterNot(t => Flags.isInterface(t.getFlags))
        .filterNot(t => Flags.isAbstract(t.getFlags))
        .filterNot(t => Flags.isSynthetic(t.getFlags))
    }

    computeConcreteCone(jdtElement).cachedBy(jdtElement).map(SType)
  }

  override def toString: String = jdtElement.getFullyQualifiedName
}
