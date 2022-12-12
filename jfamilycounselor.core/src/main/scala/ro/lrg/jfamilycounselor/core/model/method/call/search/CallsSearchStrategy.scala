package ro.lrg.jfamilycounselor.core.model.method.call.search

import ro.lrg.jfamilycounselor.core.model.method.Method
import ro.lrg.jfamilycounselor.core.model.method.call.Call

private[method] trait CallsSearchStrategy {
  def findCalls(method: Method): List[Call]
}
