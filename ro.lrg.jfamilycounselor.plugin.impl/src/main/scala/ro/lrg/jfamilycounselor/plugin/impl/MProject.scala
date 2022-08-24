package ro.lrg.jfamilycounselor.plugin.impl

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.plugin.impl.model.project.SProject

trait MProject {
  def jdtElement: IJavaProject
  def maybeFamilyPolymorphismClients: java.util.List[MType]
}

object MProject {
  def asScala(javaProject: IJavaProject): MProject = SProject(javaProject)
}
