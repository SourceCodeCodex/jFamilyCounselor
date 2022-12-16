package ro.lrg.jfamilycounselor.core.model.method.call.search

import ro.lrg.jfamilycounselor.core.model.call.Call
import ro.lrg.jfamilycounselor.core.model.method.Method
import ro.lrg.jfamilycounselor.core.model.method.call.search.visitor.CallsVisitor
import ro.lrg.jfamilycounselor.core.util.parse.Parser
import ro.lrg.jfamilycounselor.core.util.search.JavaSearcher

private[method] object StaticCallsSearch extends CallsSearchStrategy {
  override def findCalls(method: Method): List[Call] = {
    val parentMethods = JavaSearcher.searchMethodInvocations(method.underlyingJdtObject)

    val parentMethodsAST = parentMethods.map(Parser.parse)

    parentMethodsAST
      .filter(_.isDefined)
      .flatMap(parentAST => {
        val visitor = new CallsVisitor(method.underlyingJdtObject)
        parentAST.get.accept(visitor)
        visitor.calls
      })
  }
}
