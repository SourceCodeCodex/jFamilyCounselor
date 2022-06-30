package ro.lrg.jfamilycounselor.model.project

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.metamodel.scala.{MProject, MType}
import ro.lrg.jfamilycounselor.model.`type`.SType

import scala.jdk.CollectionConverters._

final case class SProject(javaProject: IJavaProject) extends MProject {

  override val jdtElement: IJavaProject = javaProject

  override def maybeFamilyPolymorphismClients: java.util.List[MType] =
    (maybeFamilyPolymorphismClients0: List[MType]).asJava

  lazy val allTypes: List[SType] = for {
    fragment <- javaProject.getPackageFragments.toList
    compilationUnit <- fragment.getCompilationUnits.toList
    sType <- compilationUnit.getTypes.map(SType)
  } yield sType

  lazy val maybeFamilyPolymorphismClients0: List[SType] = {
    import scala.collection.parallel.CollectionConverters._

    allTypes.par.filter(_.canBeFamilyPolymorphismClient).toList
  }

  override def toString: String = javaProject.getElementName
}
