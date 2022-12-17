package ro.lrg.jfamilycounselor.core.report

import com.github.tototoshi.csv.CSVWriter
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{IProgressMonitor, IStatus, Status, SubMonitor}
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.model.project.Project
import ro.lrg.jfamilycounselor.core.util.time.TimeUtil

import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import scala.language.postfixOps

private[report] class ExportReportJob(project: Project, estimation: UsedTypesEstimation) extends Job(s"ExportReportJob: ${project.underlyingJdtObject.getElementName} ($estimation)") {

  override def run(monitor: IProgressMonitor): IStatus = {
    val workload = project.types.size * 2
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

    val (_, writeReportTime) = TimeUtil.time {
      project.types
        .par
        .foreach(t => {
          val r = t.isRelevant
          subMonitor.split(if (r) 1 else 2)

          if (r && !subMonitor.isCanceled) {
            val (ac, time) = TimeUtil.time {
              t.apertureCoverage(estimation)
            }
            println(s"AC($t) = ${f"$ac%1.3f"}   ($time)")
            synchronized {
              writer.writeRow(List(t.toString, ac.toString))
              writer.flush()
            }
            subMonitor.split(1)
          }
        })
    }


    println("Write Report " + estimation.getClass.getSimpleName + ": " + writeReportTime)

    writer.close()
    stream.close()

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = ReportExporter.jobFamily.equals(family)
}
