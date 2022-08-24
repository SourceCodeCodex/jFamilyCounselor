package ro.lrg.jfamilycounselor.plugin.impl.model.method.method_invocation.ztatic

import org.eclipse.jdt.core.IMethod
import ro.lrg.jfamilycounselor.plugin.impl.model.invocation.SInvocation
import ro.lrg.jfamilycounselor.plugin.impl.model.method.method_invocation.InvocationsSearchStrategy
import ro.lrg.jfamilycounselor.plugin.impl.model.method.method_invocation.visitor.InvocationVisitor
import ro.lrg.jfamilycounselor.plugin.impl.util.parse.Parser
import ro.lrg.jfamilycounselor.plugin.impl.util.search.JavaSearcher

object StaticInvocationsSearch extends InvocationsSearchStrategy {
  override def findInvocations(method: IMethod): List[SInvocation] = {
    val parentMethodsOfInvocation = JavaSearcher.searchMethodInvocations(method)

    val parentMethodsAST = parentMethodsOfInvocation.map(Parser.parse)

    parentMethodsAST.filter(_.isDefined).flatMap(parentAST => {
      val visitor = new InvocationVisitor(method)
      parentAST.get.accept(visitor)
      visitor.invocations
    })
  }
}
