package ro.lrg.jfamilycounselor.core.util.cache

import java.util
import java.util.Collections

private class Cache[K, V] private(private val maxSize: Int) extends java.util.LinkedHashMap[K, V] {
  override def removeEldestEntry(eldest: util.Map.Entry[K, V]): Boolean = super.size() > maxSize
}

object Cache {
  def apply[K, V](maxSize: Int): util.Map[K, V] = Collections.synchronizedMap(new Cache(maxSize))
}
