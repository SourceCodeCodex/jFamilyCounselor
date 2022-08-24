package ro.lrg.jfamilycounselor.plugin.impl.report.job

import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{IProgressMonitor, IStatus, Status, SubMonitor}
import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.SType
import ro.lrg.jfamilycounselor.plugin.impl.model.project.SProject
import ro.lrg.jfamilycounselor.plugin.impl.report.ProjectReportExporter

import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

private[report] class PossibleFamilyPolymorphismClientsJob(sProject: SProject)
    extends Job("PossibleFamilyPolymorphismClientsJob") {

  private val jobFamily = ProjectReportExporter.family

  private val buffer: ListBuffer[SType] = ListBuffer()

  lazy val result: List[SType] = buffer.toList

  override def run(monitor: IProgressMonitor): IStatus = {
    val workload = sProject.allTypes.size
    val subMonitor = SubMonitor.convert(monitor, workload)

    val possibleClients = sProject.allTypes.par
      .filter(t => {
        val cond = t.canBeFamilyPolymorphismClient
        subMonitor.split(1)
        cond
      })
      .toList

    buffer.addAll(possibleClients)

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = this.jobFamily.equals(family)
}
