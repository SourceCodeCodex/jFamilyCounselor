package ro.lrg.jfamilycounselor.impl.ref

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core._
import org.eclipse.jdt.internal.corext.util.JavaModelUtil
import ro.lrg.jfamilycounselor.impl.SType
import ro.lrg.jfamilycounselor.impl.cache.ResolvedTypesByQualifiedNameCache

private[jfamilycounselor] abstract class SRef {
  type jdtType <: IJavaElement
  def jdtElement: jdtType

  def isSusceptible: Boolean = {
    def isDeclaredTypeSus(sType: SType): Boolean = {
      val jdtType = sType.jdtElement
      jdtType.getCompilationUnit != null &&
      !jdtType.isAnonymous &&
      (jdtType.isClass || jdtType.isInterface) &&
      jdtType.getTypeParameters.isEmpty &&
      sType.concreteCone.size >= 2
    }

    declaredType0.exists(isDeclaredTypeSus)
  }

  /** This will be used solely after the [[isSusceptible]] validation
    * and therefore is safe to use in used type estimations
    */
  lazy val declaredType: SType = declaredType0.get

  /** JDT might not resolve the type based on the signature.
    * In this case, we wrap the declaring type in Option effect.
    * In order for a ref to be valid, this needs to be defined.
    * Option is used in case some computations end up in null values
    */
  lazy val declaredType0: Option[SType] = {
    val typeName: Option[String] = Some(
      JavaModelUtil.getResolvedTypeName(typeSignature, declaringType)
    ).filter(_ != null)

    val declaredType =
      typeName
        .map { name =>
          ResolvedTypesByQualifiedNameCache
            .compute(name)(fqn =>
              jdtElement.getJavaProject.findType(fqn, new NullProgressMonitor())
            )
        }
        .filter(_ != null)

    declaredType.map(new SType(_))
  }
  protected def typeSignature: String
  protected def declaringType: IType

  override def equals(other: Any): Boolean = other match {
    case that: SRef =>
      jdtElement == that.jdtElement
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(jdtElement)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
