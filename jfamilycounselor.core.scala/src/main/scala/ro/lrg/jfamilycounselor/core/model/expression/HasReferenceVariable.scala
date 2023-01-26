package ro.lrg.jfamilycounselor.core.model.expression

import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.reference.ReferenceVariable

trait HasReferenceVariable {
  def referenceVariable: ReferenceVariable

  lazy val `type`: Option[Type] = referenceVariable.`type`
}
