package ro.lrg.jfamilycounselor.plugin.impl.report.job

import com.github.tototoshi.csv.CSVWriter
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{FileLocator, IProgressMonitor, IStatus, Platform, Status, SubMonitor}
import ro.lrg.jfamilycounselor.plugin.impl.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.plugin.impl.model.project.SProject
import ro.lrg.jfamilycounselor.plugin.impl.report.ProjectReportExporter
import ro.lrg.jfamilycounselor.plugin.impl.util.TimeOps

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

private[report] class ExportReportJob(
    sProject: SProject,
    algorithm: UsedConcreteTypePairsAlgorithm
) extends Job(s"ExportReportJob-${algorithm.getClass.getSimpleName}") {

  private val jobFamily = ProjectReportExporter.family

  override def run(monitor: IProgressMonitor): IStatus = {
    val clientsJob = new PossibleFamilyPolymorphismClientsJob(sProject)
    clientsJob.setPriority(Job.LONG)
    clientsJob.setSystem(false)
    clientsJob.setUser(true)

    TimeOps.time("Compute Possible Clients") {
      clientsJob.schedule()
      clientsJob.join()
    }

    val sTypes = clientsJob.result

    val workload = sTypes.size
    val subMonitor = SubMonitor.convert(monitor, workload)

    val formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss")
    val timestamp = formatter.format(new Date())

    val url = FileLocator.resolve(
      Platform.getBundle("jFamilyCounselorPlugIn").getEntry("/")
    )

    val dirPath = s"${url.getPath}target/"
    val dir = new File(dirPath)
    dir.mkdirs()

    val name =
      s"$dirPath$sProject-${algorithm.getClass.getSimpleName}-$timestamp.csv"

    val writer = CSVWriter.open(new File(name))

    writer.writeRow(List("Class", "Aperture Coverage"))

    TimeOps.time("Write Report " + algorithm.getClass.getSimpleName) {
      sTypes.par
        .map(t => {
          writer.writeRow(
            List(t.toString, t.apertureCoverage(algorithm).toString)
          )
          synchronized(writer.flush())
          subMonitor.split(1)
        })
        .toList
        .foreach(_ => ())
    }

    writer.close()

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = this.jobFamily.equals(family)
}
