package ro.lrg.jfamilycounselor.util

import scala.concurrent.duration.DurationLong

object TimeOps {
  def time[R](opName: String)(block: => R): R = {
    val t0 = System.nanoTime().nanos
    val result = block    // call-by-name
    val t1 = System.nanoTime().nanos
    println(s"Elapsed time for $opName: ${(t1 - t0).toSeconds.seconds}")
    result
  }
}
