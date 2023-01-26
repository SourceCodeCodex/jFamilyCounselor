package ro.lrg.jfamilycounselor.core.util.cache

import java.util
import java.util.Collections
import scala.collection.mutable.ListBuffer

private class Cache[K, V] private(private val maxSize: Int) extends java.util.LinkedHashMap[K, V] {
  override def removeEldestEntry(eldest: util.Map.Entry[K, V]): Boolean = super.size() > maxSize
}

object Cache {
  private val buffer = new ListBuffer[util.Map[_, _]]

  def apply[K, V](maxSize: Int): util.Map[K, V] = {
    val r = Collections.synchronizedMap(new Cache[K, V](maxSize))
    buffer.addOne(r)
    r
  }

  def clear: Unit = buffer.foreach(_.clear())
}
