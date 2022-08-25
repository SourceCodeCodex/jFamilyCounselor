package ro.lrg.jfamilycounselor.core.report

import org.eclipse.core.runtime.jobs.{ISchedulingRule, Job}
import ro.lrg.jfamilycounselor.core.{MProject, UsedConcreteTypePairsAlgorithm}
import ro.lrg.jfamilycounselor.core.model.project.SProject
import ro.lrg.jfamilycounselor.core.report.job.ExportReportJob

object ProjectReportExporter {

  val family = "ProjectReportExporter"

  private val mutexRule: ISchedulingRule = new ISchedulingRule{
    override def contains(rule: ISchedulingRule): Boolean = rule == this

    override def isConflicting(rule: ISchedulingRule): Boolean = rule == this
  }

  def exportReport(
      mProject: MProject,
      algorithm: UsedConcreteTypePairsAlgorithm
  ): Unit = {
    val exportJob = new ExportReportJob(mProject.asInstanceOf[SProject], algorithm)
    exportJob.setPriority(Job.LONG)
    exportJob.setRule(mutexRule)
    exportJob.setSystem(false)
    exportJob.setUser(true)
    exportJob.schedule()
  }
}
