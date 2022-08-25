package ro.lrg.jfamilycounselor.core.model.`type`

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{Flags, IType}
import ro.lrg.jfamilycounselor.core.model.`type`.refs_combination.ParamCombinationStrategy
import ro.lrg.jfamilycounselor.core.model.ref.SRefPair
import ro.lrg.jfamilycounselor.core.{MRefPair, MType, UsedConcreteTypePairsAlgorithm}

import scala.jdk.CollectionConverters._

final case class SType(jdtElement: IType) extends MType {

  override def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double =
    mightHideFamilialCorrelationsRefPairs0.map(_.apertureCoverage(alg)).min

  override def mightHideFamilialCorrelationsRefPairs: java.util.List[MRefPair] =
    (mightHideFamilialCorrelationsRefPairs0: List[MRefPair]).asJava

  lazy val mightHideFamilialCorrelationsRefPairs0: List[SRefPair[_]] =
    ParamCombinationStrategy.combine(this)

  lazy val mightHideFamilialCorrelations: Boolean = {
    !jdtElement.isAnonymous &&
    jdtElement.getTypeParameters.isEmpty &&
    mightHideFamilialCorrelationsRefPairs0.nonEmpty
  }

  lazy val concreteCone: List[SType] = {
    import ro.lrg.jfamilycounselor.core.cache.implicits._

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
