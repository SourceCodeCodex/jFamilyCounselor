package ro.lrg.jfamilycounselor.core.report

import org.eclipse.core.runtime.jobs.{ISchedulingRule, Job}
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.project.Project

object ReportExporter {

  val jobFamily = "ProjectReportExporter"

  def exportReport(project: Project, estimation: UsedTypesEstimation): Unit = {
    val exportJob = new ExportReportJob(project, estimation)
    exportJob.setPriority(Job.LONG)
    exportJob.setRule(new ISchedulingRule {
      override def contains(rule: ISchedulingRule): Boolean = rule == this

      override def isConflicting(rule: ISchedulingRule): Boolean = rule == this
    })
    exportJob.setSystem(false)
    exportJob.setUser(true)
    exportJob.schedule()
  }
}
