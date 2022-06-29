package ro.lrg.jfamilycounselor.impl.model.method.invocation

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.impl.model.method.SInvocation

trait InvocationsSearchStrategy {
  def findInvocations(method: IMethod): List[SInvocation]
}
