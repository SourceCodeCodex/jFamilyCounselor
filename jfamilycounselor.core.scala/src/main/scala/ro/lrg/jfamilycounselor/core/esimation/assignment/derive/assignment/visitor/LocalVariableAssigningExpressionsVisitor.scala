package ro.lrg.jfamilycounselor.core.esimation.assignment.derive.assignment.visitor

import org.eclipse.jdt.core.ILocalVariable
import org.eclipse.jdt.core.dom.{ASTVisitor, Assignment, Expression, IBinding, IVariableBinding, SimpleName, VariableDeclarationFragment}

import scala.collection.mutable.ListBuffer

class LocalVariableAssigningExpressionsVisitor(private val localVar: ILocalVariable)
    extends ASTVisitor {
   private val assignedExpressionsBuffer: ListBuffer[Expression] = ListBuffer()

  def assignedExpressions: List[Expression] = assignedExpressionsBuffer.toList

  override def visit(node: VariableDeclarationFragment): Boolean = {
    if (Option(node.resolveBinding).isDefined && localVar == node.resolveBinding.getJavaElement)
      if (Option(node.getInitializer).isDefined)
        assignedExpressionsBuffer.addOne(node.getInitializer)
    false
  }

  override def visit(node: Assignment): Boolean = {
    val left = node.getLeftHandSide
    left match {
      case sn: SimpleName if sn.resolveBinding().getKind == IBinding.VARIABLE =>
        val varBinding = sn.resolveBinding().asInstanceOf[IVariableBinding]
        if (localVar == varBinding.getJavaElement)
          assignedExpressionsBuffer.addOne(node.getRightHandSide)
      case _ =>
    }
    true
  }
}
