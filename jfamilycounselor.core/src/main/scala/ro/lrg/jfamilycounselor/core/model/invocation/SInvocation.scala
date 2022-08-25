package ro.lrg.jfamilycounselor.core.model.invocation

import org.eclipse.jdt.core.dom.{
  ASTNode,
  ClassInstanceCreation,
  Expression,
  MethodInvocation,
  SimpleName,
  SuperMethodInvocation
}

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

  def callExpression: Option[Expression] =
    Some(node.getExpression).filter(_ != null)

  //TODO: Consult with Pepi about this
  def mightBeCalledOnSameObjectOf(m2: SMethodInvocation): Boolean = {
    (callExpression, m2.callExpression) match {
      case (None, None) => true
      case (Some(se1: SimpleName), Some(se2: SimpleName)) =>
        se1.resolveBinding().getJavaElement == se2
          .resolveBinding()
          .getJavaElement
      case _ => false
    }
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
