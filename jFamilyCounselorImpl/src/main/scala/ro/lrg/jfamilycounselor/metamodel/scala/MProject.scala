package ro.lrg.jfamilycounselor.metamodel.scala

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.model.project.SProject

trait MProject {
  def jdtElement: IJavaProject
  def maybeFamilyPolymorphismClients: java.util.List[MType]
}

object MProject {
  def asScala(javaProject: IJavaProject): MProject = SProject(javaProject)
}
