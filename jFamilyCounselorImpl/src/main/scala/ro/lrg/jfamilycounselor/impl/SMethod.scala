package ro.lrg.jfamilycounselor.impl

import org.eclipse.jdt.core.IMethod

private[jfamilycounselor] final class SMethod(method: IMethod) {

  val jdtElement: IMethod = method

  override def toString: String = method.getElementName

  override def equals(other: Any): Boolean = other match {
    case that: SMethod =>
      jdtElement == that.jdtElement
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(method)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
