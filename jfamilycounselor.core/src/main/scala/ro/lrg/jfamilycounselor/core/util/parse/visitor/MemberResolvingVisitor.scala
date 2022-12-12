package ro.lrg.jfamilycounselor.core.util.parse.visitor

import org.eclipse.jdt.core.IMember
import org.eclipse.jdt.core.dom.{ASTNode, ASTVisitor}

private[parse] abstract class MemberResolvingVisitor[N <: ASTNode](protected val member: IMember) extends ASTVisitor {
  def result: N
}
