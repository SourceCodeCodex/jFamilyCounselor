package ro.lrg.jfamilycounselor.strategies.method_invocation.ztatic

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.model.invocation.SInvocation
import ro.lrg.jfamilycounselor.strategies.method_invocation.InvocationsSearchStrategy
import ro.lrg.jfamilycounselor.strategies.method_invocation.visitor.InvocationVisitor
import ro.lrg.jfamilycounselor.util.parse.Parser
import ro.lrg.jfamilycounselor.util.search.JavaSearcher

object StaticInvocationsSearch extends InvocationsSearchStrategy {
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
