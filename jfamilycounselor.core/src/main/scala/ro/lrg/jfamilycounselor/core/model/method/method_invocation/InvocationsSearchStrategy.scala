package ro.lrg.jfamilycounselor.core.model.method.method_invocation

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.core.model.invocation.SInvocation

trait InvocationsSearchStrategy {
  def findInvocations(method: IMethod): List[SInvocation]
}
