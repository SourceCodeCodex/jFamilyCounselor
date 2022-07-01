package ro.lrg.jfamilycounselor.model.invocation

import org.eclipse.jdt.core.dom.{ASTNode, ClassInstanceCreation, Expression, MethodInvocation, Name, SuperMethodInvocation}

import scala.jdk.CollectionConverters.ListHasAsScala

sealed trait SInvocation {
  type N <: ASTNode
  def node: N

  override def toString: String = node.toString

  def argAtIndex(index: Int): Expression
}

final case class SClassInstanceCreation(node: ClassInstanceCreation)
    extends SInvocation {
  override type N = ClassInstanceCreation

  override def argAtIndex(index: Int): Expression =
    node.arguments().asScala.toList.asInstanceOf[List[Expression]](index)
}

final case class SMethodInvocation(node: MethodInvocation) extends SInvocation {
  override type N = MethodInvocation

  def callExpression: Expression = node.getExpression

  //TODO: Consult with Pepi about this
  def mightBeCalledOnSameObjectOf(m2: SMethodInvocation): Boolean =
    if(callExpression.getNodeType != callExpression.getNodeType)
      false
    else (callExpression, m2.callExpression) match {
      case (n1: Name, n2: Name)  => n1.resolveBinding().getJavaElement == n2.resolveBinding().getJavaElement
      case _ => false
    }

  override def argAtIndex(index: Int): Expression =
    node.arguments().asScala.toList.asInstanceOf[List[Expression]](index)
}

final case class SSuperMethodInvocation(node: SuperMethodInvocation)
    extends SInvocation {
  override type N = SuperMethodInvocation

  override def argAtIndex(index: Int): Expression =
    node.arguments().asScala.toList.asInstanceOf[List[Expression]](index)
}
