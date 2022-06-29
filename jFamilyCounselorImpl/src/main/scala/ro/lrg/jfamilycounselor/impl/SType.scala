package ro.lrg.jfamilycounselor.impl

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{Flags, IType}
import ro.lrg.jfamilycounselor.alg.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.impl.cache.ConcreteConeOfTypeCache
import ro.lrg.jfamilycounselor.impl.pair.SRefPair
import ro.lrg.jfamilycounselor.{MRefPair, MType}

import scala.jdk.CollectionConverters._

private[jfamilycounselor] final class SType(`type`: IType) extends MType {

  override val jdtElement: IType = `type`

  override def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double =
    susceptibleRefPairs0.map(_.apertureCoverage(alg)).min

  override def susceptibleRefPairs: java.util.List[MRefPair] =
    (susceptibleRefPairs0: List[MRefPair]).asJava

  /** All pairing logic is relocated using class extension in
    * [[ro.lrg.jfamilycounselor.impl.pair.SRefPairSyntax]]
    * as it might change in the future. This is done in order to provide
    * flexibility on [[SRefPair]] creation.
    */
  lazy val susceptibleRefPairs0: List[SRefPair[_]] = {
    import ro.lrg.jfamilycounselor.impl.pair.SRefPairSyntax._
    this.paramPairs
  }

  lazy val canBeFamilyPolymorphismClient: Boolean = {
    !`type`.isAnonymous &&
    `type`.getTypeParameters.isEmpty &&
    susceptibleRefPairs0.nonEmpty
  }

  lazy val concreteCone: List[SType] = {
    def computeConcreteCone(tpe: IType): List[IType] = {
      val subtypes = tpe
        .newTypeHierarchy(new NullProgressMonitor())
        .getAllSubtypes(tpe)
        .toList

      (`type` :: subtypes)
        .filterNot(t => t.isAnonymous)
        .filterNot(t => Flags.isInterface(t.getFlags))
        .filterNot(t => Flags.isAbstract(t.getFlags))
        .filterNot(t => Flags.isSynthetic(t.getFlags))
    }

    ConcreteConeOfTypeCache
      .compute(`type`)(computeConcreteCone)
      .map(new SType(_))
  }

  override def toString: String = `type`.getFullyQualifiedName

  override def equals(other: Any): Boolean = other match {
    case that: SType =>
      jdtElement == that.jdtElement
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(`type`)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
