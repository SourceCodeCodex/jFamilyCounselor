package ro.lrg.jfamilycounselor.core

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.core.model.project.SProject

trait MProject {
  def jdtElement: IJavaProject
  def mightHideFamilialCorrelationsClasses: java.util.List[MType]
}

object MProject {
  def asScala(javaProject: IJavaProject): MProject = SProject(javaProject)
}
