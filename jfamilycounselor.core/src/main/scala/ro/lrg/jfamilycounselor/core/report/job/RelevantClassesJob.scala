package ro.lrg.jfamilycounselor.core.report.job

import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{IProgressMonitor, IStatus, Status, SubMonitor}
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.project.Project
import ro.lrg.jfamilycounselor.core.report.ReportExporter

import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

private[report] class RelevantClassesJob(project: Project)
  extends Job("RelevantClassesJob: " + project.underlyingJdtObject.getElementName) {

  private val jobFamily = ReportExporter.jobFamily

  private val buffer: ListBuffer[Type] = ListBuffer()

  lazy val result: List[Type] = buffer.toList

  override def run(monitor: IProgressMonitor): IStatus = {
    val workload = project.types.size
    val subMonitor = SubMonitor.convert(monitor, workload)

    val possibleClients = project.types
      .par
      .filter(_.isRelevant)
      .map { t => subMonitor.split(1); t }

    buffer.addAll(possibleClients)

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = this.jobFamily.equals(family)
}
