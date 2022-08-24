package ro.lrg.jfamilycounselor.plugin.impl.usedtypesalgorithm.assignments.model.assgn.visitor

import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom.{ASTVisitor, Assignment, Expression, IBinding, IVariableBinding, SimpleName, VariableDeclarationFragment}

import scala.collection.mutable.ListBuffer

class LocalVarAssignmentsVisitor(private val localVar: ILocalVariable)
    extends ASTVisitor {
  protected val assignmentsBuffer: ListBuffer[Expression] = ListBuffer()

  def assignments: List[Expression] = assignmentsBuffer.toList

  override def visit(node: VariableDeclarationFragment): Boolean = {
    val binding = node.resolveBinding
    if (binding != null && localVar == binding.getJavaElement)
      if (node.getInitializer != null)
        assignmentsBuffer.addOne(node.getInitializer)
    false
  }

  override def visit(node: Assignment): Boolean = {
    val left = node.getLeftHandSide
    left match {
      case sn: SimpleName if sn.resolveBinding().getKind == IBinding.VARIABLE =>
        val varBinding = sn.resolveBinding().asInstanceOf[IVariableBinding]
        if (localVar == varBinding.getJavaElement)
          assignmentsBuffer.addOne(node.getRightHandSide)
      case _ =>
    }
    true
  }
}
