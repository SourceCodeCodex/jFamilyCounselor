package ro.lrg.jfamilycounselor.strategies.method_invocation

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.model.invocation.SInvocation

trait InvocationsSearchStrategy {
  def findInvocations(method: IMethod): List[SInvocation]
}
