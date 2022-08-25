package ro.lrg.jfamilycounselor.core.model.project

import org.eclipse.jdt.core.{ICompilationUnit, IJavaProject, IPackageFragment}
import ro.lrg.jfamilycounselor.core.model.`type`.SType
import ro.lrg.jfamilycounselor.core.{MProject, MType}

import scala.jdk.CollectionConverters._

final case class SProject(javaProject: IJavaProject) extends MProject {

  override val jdtElement: IJavaProject = javaProject

  override def mightHideFamilialCorrelationsClasses: java.util.List[MType] =
    (mightHideFamilialCorrelationsClasses0: List[MType]).asJava

  lazy val allTypes: List[SType] = for {
    fragment <- javaProject.getPackageFragments.toList
    compilationUnit <- fragment.getCompilationUnits.toList
    sType <- compilationUnit.getTypes.map(SType)
  } yield sType

  lazy val mightHideFamilialCorrelationsClasses0: List[SType] = {
    import scala.collection.parallel.CollectionConverters._

    allTypes.par.filter(_.mightHideFamilialCorrelations).toList
  }

  override def toString: String = javaProject.getElementName
}
