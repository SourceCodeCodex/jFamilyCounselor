package ro.lrg.jfamilycounselor.util.parse

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{ICompilationUnit, IMember, IMethod}
import org.eclipse.jdt.core.dom.{AST, ASTNode, ASTParser, CompilationUnit, MethodDeclaration}
import ro.lrg.jfamilycounselor.util.parse.visitor.MethodDeclarationVisitor

object Parser {
  import ro.lrg.jfamilycounselor.cache.implicits._

  def parse(compilationUnit: ICompilationUnit): CompilationUnit = {
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

    parseCompilationUnit(compilationUnit).cachedBy(compilationUnit)
  }

  def parse(method: IMethod): MethodDeclaration = {
    val visitor = new MethodDeclarationVisitor(method)
    parseMember(method, visitor)
  }

  private def parseMember[N <: ASTNode](
      member: IMember,
      visitor: MemberResolvingVisitor[N]
  ): N = {
    def parseMember(member: IMember): ASTNode = {
      val cuAST = parse(member.getCompilationUnit)
      cuAST.accept(visitor)
      visitor.result
    }

    parseMember(member).cachedBy(member).asInstanceOf[N]
  }

}
