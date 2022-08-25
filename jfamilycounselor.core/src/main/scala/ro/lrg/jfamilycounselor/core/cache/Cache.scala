package ro.lrg.jfamilycounselor.core.cache

class Cache[I, O] {
  private val cache: scala.collection.mutable.Map[I, O] = scala.collection.concurrent.TrieMap()

  def cache(input: I)(computation: => O): O =
    if (cache.contains(input))
      cache(input)
    else {
      val output = computation
      cache.put(input, output)
      output
    }

  def clear(): Unit = cache.clear()
}

object Cache {
  def clearAllCaches(): Unit =
    List(
      implicits.concreteConeOfTypeCache,
      implicits.resolvedTypesByQualifiedNameCache,
      implicits.cuParsingCache,
      implicits.methodInvocationParentsCache,
      implicits.memberParsingCache
    ).foreach(_.clear())
}
