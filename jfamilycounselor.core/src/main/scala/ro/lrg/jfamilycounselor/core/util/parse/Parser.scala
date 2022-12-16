package ro.lrg.jfamilycounselor.core.util.parse

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.{ICompilationUnit, IMember, IMethod}
import ro.lrg.jfamilycounselor.core.util.parse.visitor.{MemberResolvingVisitor, MethodDeclarationVisitor}

object Parser {
  def parse(method: IMethod): Option[MethodDeclaration] =
    parseMember(method, new MethodDeclarationVisitor(method))

  def parse(compilationUnit: ICompilationUnit): Option[CompilationUnit] = {
    val parser = ASTParser.newParser(AST.getJLSLatest)
    parser.setSource(compilationUnit)
    parser.setProject(compilationUnit.getJavaProject)
    parser.setKind(ASTParser.K_COMPILATION_UNIT)
    parser.setResolveBindings(true)
    Option(
      parser.createAST(new NullProgressMonitor()).asInstanceOf[CompilationUnit]
    )
  }

  private def parseMember[N <: ASTNode](member: IMember, visitor: MemberResolvingVisitor[N]): Option[N] =
    parse(member.getCompilationUnit)
      .flatMap(cuAST => {
        cuAST.accept(visitor)
        Option(visitor.result)
      })
}
