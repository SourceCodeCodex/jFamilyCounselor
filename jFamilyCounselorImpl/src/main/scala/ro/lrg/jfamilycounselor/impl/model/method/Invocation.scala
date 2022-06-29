package ro.lrg.jfamilycounselor.impl.model.method

import org.eclipse.jdt.core.dom.{
  ASTNode,
  ClassInstanceCreation,
  MethodInvocation,
  SuperMethodInvocation
}

sealed trait SInvocation {
  type N <: ASTNode
  def node: N

  override def toString: String = node.toString
}

case class SClassInstanceCreation(node: ClassInstanceCreation)
    extends SInvocation {
  override type N = ClassInstanceCreation
}

case class SMethodInvocation(node: MethodInvocation) extends SInvocation {
  override type N = MethodInvocation
}

case class SSuperMethodInvocation(node: SuperMethodInvocation)
    extends SInvocation {
  override type N = SuperMethodInvocation
}
