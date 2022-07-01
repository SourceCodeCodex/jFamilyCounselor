package ro.lrg.jfamilycounselor.cache

import org.eclipse.jdt.core.dom.{ASTNode, CompilationUnit}
import org.eclipse.jdt.core.{ICompilationUnit, IMember, IMethod, IType}

object implicits {
  implicit class ComputationSyntaxOps[I, O](computation: => O) {
    def cachedBy(input: I)(implicit cache: Cache[I, O]): O =
      cache.cache(input)(computation)
  }

  implicit val resolvedTypesByQualifiedNameCache: Cache[String, IType] =
    new Cache[String, IType]
  implicit val concreteConeOfTypeCache: Cache[IType, List[IType]] =
    new Cache[IType, List[IType]]
  implicit val methodInvocationParentsCache: Cache[IMethod, List[IMethod]] =
    new Cache[IMethod, List[IMethod]]
  implicit val cuParsingCache: Cache[ICompilationUnit, Option[CompilationUnit]] =
    new Cache[ICompilationUnit, Option[CompilationUnit]]
  implicit val memberParsingCache: Cache[IMember, Option[ASTNode]] =
    new Cache[IMember, Option[ASTNode]]

}
