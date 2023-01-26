package ro.lrg.jfamilycounselor.core.model.method.call.search.visitor

import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.dom._
import ro.lrg.jfamilycounselor.core.model.call.{Call, Instantiation, MethodCall, SuperMethodCall}

import scala.collection.mutable.ListBuffer

private[search]  class CallsVisitor(private val method: IMethod)
  extends ASTVisitor {

  private val result: ListBuffer[Call] = ListBuffer()

  def calls: List[Call] = result.toList

  override def visit(node: ClassInstanceCreation): Boolean = {
    if (node.resolveConstructorBinding().getJavaElement == method)
      result.addOne(Instantiation(node))
    false
  }

  override def visit(node: MethodInvocation): Boolean = {
    if (node.resolveMethodBinding().getJavaElement == method)
      result.addOne(MethodCall(node))
    false
  }

  override def visit(node: SuperMethodInvocation): Boolean = {
    if (node.resolveMethodBinding().getJavaElement == method)
      result.addOne(SuperMethodCall(node))
    false
  }

}
