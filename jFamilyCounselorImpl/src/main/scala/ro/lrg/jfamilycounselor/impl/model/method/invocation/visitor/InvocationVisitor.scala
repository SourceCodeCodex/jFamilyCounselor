package ro.lrg.jfamilycounselor.impl.model.method.invocation.visitor

import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.dom._
import ro.lrg.jfamilycounselor.impl.model.method._

import scala.collection.mutable.ListBuffer

private[invocation] class InvocationVisitor(private val method: IMethod)
    extends ASTVisitor {

  private val result0: ListBuffer[SInvocation] = ListBuffer()

  def invocations: List[SInvocation] = result0.toList

  override def visit(node: ClassInstanceCreation): Boolean = {
    if (node.resolveConstructorBinding().getJavaElement == method)
      result0.addOne(SClassInstanceCreation(node))
    false
  }

  override def visit(node: MethodInvocation): Boolean = {
    if (node.resolveMethodBinding().getJavaElement == method)
      result0.addOne(SMethodInvocation(node))
    false
  }

  override def visit(node: SuperMethodInvocation): Boolean = {
    if (node.resolveMethodBinding().getJavaElement == method)
      result0.addOne(SSuperMethodInvocation(node))
    false
  }

}
