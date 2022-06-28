package ro.lrg.jfamilycounselor.impl

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{Flags, IType}
import ro.lrg.jfamilycounselor.impl.pair.SRefPair
import ro.lrg.jfamilycounselor.{MRefPair, MType, UsedConcreteTypePairsAlgorithm}

import scala.jdk.CollectionConverters._

private[jfamilycounselor] final class SType(`type`: IType)
    extends MType {

  override val jdtElement: IType = `type`

  override def apertureCoverage(alg: UsedConcreteTypePairsAlgorithm): Double =
    susceptibleRefPairs0.map(_.apertureCoverage(alg)).min

  override def susceptibleRefPairs: java.util.List[MRefPair] =
    (susceptibleRefPairs0: List[MRefPair]).asJava

  /**
   * All pairing logic is relocated using class extension in
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

  lazy val cone: List[SType] = {
    val subtypes = `type`
      .newTypeHierarchy(new NullProgressMonitor())
      .getAllSubtypes(`type`)
      .toList
    (`type` :: subtypes).map(new SType(_))
  }

  lazy val concreteCone: List[SType] = {
    cone.map(_.jdtElement)
      .filterNot(_.isAnonymous)
      .filterNot(t => Flags.isInterface(t.getFlags))
      .filterNot(t => Flags.isAbstract(t.getFlags))
      .filterNot(t => Flags.isSynthetic(t.getFlags))
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
