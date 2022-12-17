package ro.lrg.jfamilycounselor.core.util.parse

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.{ICompilationUnit, IMember, IMethod}
import ro.lrg.jfamilycounselor.core.util.cache.Cache
import ro.lrg.jfamilycounselor.core.util.parse.visitor.{MemberResolvingVisitor, MethodDeclarationVisitor}

object Parser {
  private val cache = Cache[ICompilationUnit, CompilationUnit](1024)

  def parse(method: IMethod): Option[MethodDeclaration] =
    parseMember(method, new MethodDeclarationVisitor(method))

  def parse(compilationUnit: ICompilationUnit): Option[CompilationUnit] =
    if (cache.containsKey(compilationUnit))
      Some(cache.get(compilationUnit))
    else {
      val parser = ASTParser.newParser(AST.getJLSLatest)
      parser.setSource(compilationUnit)
      parser.setProject(compilationUnit.getJavaProject)
      parser.setKind(ASTParser.K_COMPILATION_UNIT)
      parser.setResolveBindings(true)

      for {
        ast <- Option(parser.createAST(new NullProgressMonitor()).asInstanceOf[CompilationUnit])
        _ = cache.put(compilationUnit, ast)
      } yield ast
    }

  private def parseMember[N <: ASTNode](member: IMember, visitor: MemberResolvingVisitor[N]): Option[N] =
    parse(member.getCompilationUnit)
      .flatMap(cuAST => {
        cuAST.accept(visitor)
        Option(visitor.result)
      })
}
