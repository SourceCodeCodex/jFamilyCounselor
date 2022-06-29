package ro.lrg.jfamilycounselor.impl.model.project

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.impl.model.`type`.SType
import ro.lrg.jfamilycounselor.{MProject, MType}

import scala.jdk.CollectionConverters._

private[jfamilycounselor] final class SProject(javaProject: IJavaProject)
    extends MProject {

  override val jdtElement: IJavaProject = javaProject

  override def maybeFamilyPolymorphismClients: java.util.List[MType] =
    (maybeFamilyPolymorphismClients0: List[MType]).asJava

  lazy val allTypes: List[SType] = for {
    fragment <- javaProject.getPackageFragments.toList
    compilationUnit <- fragment.getCompilationUnits.toList
    sType <- compilationUnit.getTypes.map(new SType(_))
  } yield sType

  lazy val maybeFamilyPolymorphismClients0: List[SType] = {
    import scala.collection.parallel.CollectionConverters._

    allTypes.par.filter(_.canBeFamilyPolymorphismClient).toList
  }

  override def toString: String = javaProject.getElementName

  override def equals(other: Any): Boolean = other match {
    case that: SProject =>
      jdtElement == that.jdtElement
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(javaProject)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
