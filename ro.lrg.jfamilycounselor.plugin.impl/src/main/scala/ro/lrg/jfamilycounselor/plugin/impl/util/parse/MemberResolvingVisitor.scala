package ro.lrg.jfamilycounselor.plugin.impl.util.parse

import org.eclipse.jdt.core.IMember
import org.eclipse.jdt.core.dom.{ASTNode, ASTVisitor}

abstract class MemberResolvingVisitor[N <: ASTNode](
    protected val member: IMember
) extends ASTVisitor {
  def result: N
}