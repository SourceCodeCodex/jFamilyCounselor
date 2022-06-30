package ro.lrg.jfamilycounselor.model.invocation

import org.eclipse.jdt.core.dom.{ASTNode, ClassInstanceCreation, Expression, MethodInvocation, SuperMethodInvocation}

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

  override def argAtIndex(index: Int): Expression =
    node.arguments().asScala.toList.asInstanceOf[List[Expression]](index)
}

final case class SSuperMethodInvocation(node: SuperMethodInvocation)
    extends SInvocation {
  override type N = SuperMethodInvocation

  override def argAtIndex(index: Int): Expression =
    node.arguments().asScala.toList.asInstanceOf[List[Expression]](index)
}
