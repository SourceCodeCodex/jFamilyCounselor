package ro.lrg.jfamilycounselor.core.model.method.call.search

import ro.lrg.jfamilycounselor.core.model.call.Call
import ro.lrg.jfamilycounselor.core.model.method.Method

private[method] trait CallsSearchStrategy {
  def findCalls(method: Method): List[Call]
}
