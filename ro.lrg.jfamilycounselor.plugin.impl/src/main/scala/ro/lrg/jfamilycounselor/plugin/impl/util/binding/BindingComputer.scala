package ro.lrg.jfamilycounselor.plugin.impl.util.binding

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.core.dom.{AST, ASTParser, IBinding}

object BindingComputer {
  def binding[B <: IBinding](javaElement: IJavaElement): B = {
    val parser = ASTParser.newParser(AST.getJLSLatest)
    parser.setProject(javaElement.getJavaProject)
    val binding =
      parser.createBindings(Array(javaElement), new NullProgressMonitor())(0)
    binding.asInstanceOf[B]
  }
}
