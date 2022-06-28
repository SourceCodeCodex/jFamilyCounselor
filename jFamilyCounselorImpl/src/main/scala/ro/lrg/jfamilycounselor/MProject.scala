package ro.lrg.jfamilycounselor

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.impl.SProject

trait MProject {
  def jdtElement: IJavaProject
  def maybeFamilyPolymorphismClients: java.util.List[MType]
}

object MProject {
  def apply(javaProject: IJavaProject): MProject = new SProject(javaProject)
}
