package ro.lrg.jfamilycounselor.plugin.impl.model.method.method_invocation

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.plugin.impl.model.invocation.SInvocation

trait InvocationsSearchStrategy {
  def findInvocations(method: IMethod): List[SInvocation]
}
