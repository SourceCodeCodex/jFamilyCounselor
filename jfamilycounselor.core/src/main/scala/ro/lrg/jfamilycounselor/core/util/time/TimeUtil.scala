package ro.lrg.jfamilycounselor.core.util.time

import scala.concurrent.duration.{DurationLong, FiniteDuration}

object TimeUtil {
  def time[R](block: () => R): (R, FiniteDuration) = {
    val t0 = System.nanoTime().nanos
    val result = block()
    val t1 = System.nanoTime().nanos
    (result, t1 - t0)
  }
}
