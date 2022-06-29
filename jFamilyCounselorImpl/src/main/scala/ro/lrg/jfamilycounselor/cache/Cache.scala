package ro.lrg.jfamilycounselor.cache

import ro.lrg.jfamilycounselor.impl.cache.{
  ConcreteConeOfTypeCache,
  ResolvedTypesByQualifiedNameCache
}

trait Cache[I, O] {
  private val cache: scala.collection.mutable.Map[I, O] = scala.collection.concurrent.TrieMap()

  def compute(input: I)(computation: I => O): O =
    if (cache.contains(input))
      cache(input)
    else {
      val output = computation(input)
      cache.put(input, output)
      output
    }

  def clear(): Unit = cache.clear()
}

object Cache {
  def clearAllCaches(): Unit =
    List(
      ResolvedTypesByQualifiedNameCache,
      ConcreteConeOfTypeCache
    ).foreach(_.clear())
}
