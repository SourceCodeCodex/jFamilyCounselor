package ro.lrg.jfamilycounselor.core.report.job

import com.github.tototoshi.csv.CSVWriter
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{IProgressMonitor, IStatus, Status, SubMonitor}
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.project.Project
import ro.lrg.jfamilycounselor.core.report.ReportExporter
import ro.lrg.jfamilycounselor.core.util.time.TimeUtil

import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

private[report] class ExportReportJob(project: Project, estimation: UsedTypesEstimation) extends Job(s"ExportReportJob: ${project.underlyingJdtObject.getElementName} ($estimation)") {

  private val jobFamily = ReportExporter.jobFamily

  override def run(monitor: IProgressMonitor): IStatus = {
    val clientsJob = new RelevantClassesJob(project)
    clientsJob.setPriority(Job.LONG)
    clientsJob.setSystem(false)
    clientsJob.setUser(true)

    val (_, computePossibleClientsTime) = TimeUtil.time { () =>
      clientsJob.schedule()
      clientsJob.join()
    }

    println("Compute Possible Clients: " + computePossibleClientsTime)

    val types = clientsJob.result

    val workload = types.size
    val subMonitor = SubMonitor.convert(monitor, workload)

    val formatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss")
    val timestamp = formatter.format(new Date())

    val wsRoot = project.underlyingJdtObject.getProject.getWorkspace.getRoot

    val outputFolder = wsRoot.getFolder(project.underlyingJdtObject.getOutputLocation).getLocation
    val reportsFolder = outputFolder.append("jFamilyCounselor-reports")
    reportsFolder.toFile.mkdirs()

    val outputFile = reportsFolder.append(s"${project.underlyingJdtObject.getElementName}-${estimation.getClass.getSimpleName.replace("$", "")}-$timestamp.csv").toFile
    outputFile.createNewFile()

    val stream = new FileOutputStream(outputFile)

    val writer = CSVWriter.open(stream)

    writer.writeRow(List("Class", "Aperture Coverage"))

    val (_, writeReportTime) = TimeUtil.time { () =>
      types.par
        .foreach(t => {
          val ac = t.apertureCoverage(estimation)
          synchronized {
            writer.writeRow(
              List(t.toString, ac.toString)
            )
            writer.flush()
          }
          subMonitor.split(1)
        })
    }

    println("Write Report " + estimation.getClass.getSimpleName + ": " + writeReportTime)

    writer.close()
    stream.close()

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = this.jobFamily.equals(family)
}
