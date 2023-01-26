package ro.lrg.jfamilycounselor.core.esimation.assignment.config

import scala.concurrent.duration.{DurationDouble, FiniteDuration}
import scala.language.postfixOps

object AssignmentsEstimationConfig {
  val MAX_COMBINATIONS: Int = 1

  val MAX_DEPTH: Int = 3

  val DERIVATION_TIMEOUT: FiniteDuration = 180 seconds
}
