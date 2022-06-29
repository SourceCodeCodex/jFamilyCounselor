package ro.lrg.jfamilycounselor.impl.model.method.invocation.ztatic

import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.dom.MethodInvocation
import ro.lrg.jfamilycounselor.impl.model.method.SInvocation
import ro.lrg.jfamilycounselor.impl.model.method.invocation.InvocationsSearchStrategy
import ro.lrg.jfamilycounselor.impl.model.method.invocation.visitor.InvocationVisitor
import ro.lrg.jfamilycounselor.impl.util.parse.Parser
import ro.lrg.jfamilycounselor.impl.util.search.JavaSearcher

private[method] object StaticInvocationsSearch extends InvocationsSearchStrategy {
  override def findInvocations(method: IMethod): List[SInvocation] = {
    val parentsMethodsOfInvoc = JavaSearcher.searchMethodInvocations(method)

    val parentMethodsAST = parentsMethodsOfInvoc.map(Parser.parse)

    parentMethodsAST.flatMap(parentAST => {
      val visitor = new InvocationVisitor(method)
      parentAST.accept(visitor)
      visitor.invocations
    })
  }
}
