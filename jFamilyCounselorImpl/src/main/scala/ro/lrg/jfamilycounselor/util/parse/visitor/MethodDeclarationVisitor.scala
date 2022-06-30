package ro.lrg.jfamilycounselor.util.parse.visitor

import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.dom.MethodDeclaration
import ro.lrg.jfamilycounselor.util.parse.MemberResolvingVisitor

class MethodDeclarationVisitor(method: IMethod)
    extends MemberResolvingVisitor[MethodDeclaration](method) {
  var result: MethodDeclaration = _

  override def visit(node: MethodDeclaration): Boolean = {
    if (member == node.resolveBinding.getJavaElement)
      result = node
    true
  }

}
