package ro.lrg.jfamilycounselor.core.report

import com.github.tototoshi.csv.CSVWriter
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.{IProgressMonitor, IStatus, Status, SubMonitor}
import ro.lrg.jfamilycounselor.core.esimation.UsedTypesEstimation
import ro.lrg.jfamilycounselor.core.esimation.assignment.AssignmentsBased
import ro.lrg.jfamilycounselor.core.esimation.name.NameBased
import ro.lrg.jfamilycounselor.core.model.project.Project
import ro.lrg.jfamilycounselor.core.util.cache.Cache
import ro.lrg.jfamilycounselor.core.util.time.TimeUtil

import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import java.util.concurrent.{ConcurrentLinkedQueue, ForkJoinPool}
import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
import scala.collection.parallel.ForkJoinTaskSupport
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

    val parallelism = estimation match {
      case AssignmentsBased => 2
      case NameBased => Runtime.getRuntime.availableProcessors
      case _ => Runtime.getRuntime.availableProcessors / 2
    }

    val taskSupport: ForkJoinTaskSupport = new ForkJoinTaskSupport(new ForkJoinPool(parallelism))

    val concurrentLinkedQueue = new ConcurrentLinkedQueue[List[String]]()
    val condition = new AtomicBoolean

    val counter = new AtomicInteger(0)

    condition.set(true)

    taskSupport.forkJoinPool.submit(new Runnable {
      override def run(): Unit = {
        println("WriteToDisk running on: " + Thread.currentThread().getName)
        while (condition.get() || !concurrentLinkedQueue.isEmpty) {
          if (!concurrentLinkedQueue.isEmpty) {
            val row = concurrentLinkedQueue.poll()
            //println(row.mkString(","))
            subMonitor.split(1)
            writer.writeRow(row)
            writer.flush()
          }
        }
      }
    })


    val (_, writeReportTime) = TimeUtil.time {
      val types = project.types.par
      types.tasksupport = taskSupport
      types.foreach(t => {
        if (!subMonitor.isCanceled) {
          val r = t.isRelevant
          subMonitor.split(if (r) 1 else 2)

          if (r) {
            println(counter.getAndIncrement() + " " + t.toString)
            val ac = t.apertureCoverage(estimation)
            concurrentLinkedQueue.add(List(t.toString, ac.toString))
          }
        }
      })
    }

    println("Write Report " + estimation.getClass.getSimpleName + ": " + writeReportTime)

    writer.close()
    stream.close()

    condition.set(false)

    taskSupport.forkJoinPool.shutdown()

    Cache.clear

    Status.OK_STATUS
  }

  override def belongsTo(family: Any): Boolean = ReportExporter.jobFamily.equals(family)

  UsedTypesEstimation.NAME_BASED
}
