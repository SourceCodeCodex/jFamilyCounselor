package ro.lrg.jfamilycounselor.core.util.time

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.duration.{DurationLong, FiniteDuration}

object TimeUtil {
  def time[R](block: => R): (R, FiniteDuration) = {
    val t0 = Instant.now()
    val result = block
    val t1 = Instant.now()
    (result, t0.until(t1, ChronoUnit.SECONDS).second)
  }
}
