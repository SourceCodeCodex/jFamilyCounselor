package ro.lrg.jfamilycounselor.core.report.job

import com.github.tototoshi.csv.CSVWriter
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{IProgressMonitor, IStatus, Status, SubMonitor}
import ro.lrg.jfamilycounselor.core.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.core.model.project.SProject
import ro.lrg.jfamilycounselor.core.report.ProjectReportExporter
import ro.lrg.jfamilycounselor.core.util.TimeOps

import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

private[report] class ExportReportJob(
    sProject: SProject,
    algorithm: UsedConcreteTypePairsAlgorithm
) extends Job(s"ExportReportJob-${algorithm.getClass.getSimpleName}") {

  private val jobFamily = ProjectReportExporter.family

  override def run(monitor: IProgressMonitor): IStatus = {
    val clientsJob = new MightHideFamilialCorrelationsClassesJob(sProject)
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

    val wsRoot = sProject.javaProject.getProject.getWorkspace.getRoot

    val outputFolder = wsRoot.getFolder(sProject.javaProject.getOutputLocation).getLocation
    val reportsFolder = outputFolder.append("jFamilyCounselor-reports")
    reportsFolder.toFile.mkdirs()

    val outputFile = reportsFolder.append(s"$sProject-${algorithm.getClass.getSimpleName.replace("$", "")}-$timestamp.csv").toFile
    outputFile.createNewFile()

    val stream = new FileOutputStream(outputFile)

    val writer = CSVWriter.open(stream)

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
    stream.close()

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = this.jobFamily.equals(family)
}
