package ro.lrg.jfamilycounselor.core.util.parse

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{ICompilationUnit, IMember, IMethod}
import org.eclipse.jdt.core.dom.{AST, ASTNode, ASTParser, CompilationUnit, MethodDeclaration}
import ro.lrg.jfamilycounselor.core.util.parse.visitor.MethodDeclarationVisitor

object Parser {
  import ro.lrg.jfamilycounselor.core.cache.implicits._

  def parse(compilationUnit: ICompilationUnit): Option[CompilationUnit] = {
    def parseCompilationUnit(
        compilationUnit: ICompilationUnit
    ): CompilationUnit = {
      val parser = ASTParser.newParser(AST.getJLSLatest)
      parser.setSource(compilationUnit)
      parser.setProject(compilationUnit.getJavaProject)
      parser.setKind(ASTParser.K_COMPILATION_UNIT)
      parser.setResolveBindings(true)
      parser.createAST(new NullProgressMonitor()).asInstanceOf[CompilationUnit]
    }

    Some(parseCompilationUnit(compilationUnit)).filter(_ != null).cachedBy(compilationUnit)
  }

  def parse(method: IMethod): Option[MethodDeclaration] = {
    val visitor = new MethodDeclarationVisitor(method)
    parseMember(method, visitor)
  }

  private def parseMember[N <: ASTNode](
      member: IMember,
      visitor: MemberResolvingVisitor[N]
  ): Option[N] = {
    def parseMember(member: IMember): Option[ASTNode] =
      parse(member.getCompilationUnit).map(cuAST => {
        cuAST.accept(visitor)
        visitor.result
      })


    parseMember(member).cachedBy(member)
      .filter(_ != null)
      .asInstanceOf[Option[N]]
  }

}
