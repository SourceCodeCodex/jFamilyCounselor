package ro.lrg.jfamilycounselor.impl.ref

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core._
import org.eclipse.jdt.internal.corext.util.JavaModelUtil
import ro.lrg.jfamilycounselor.impl.SType
import ro.lrg.jfamilycounselor.impl.util.RefSusceptibilityChecker

private[jfamilycounselor] abstract class SRef {
  type jdtType <: IJavaElement
  def jdtElement: jdtType

  def isSusceptible: Boolean = RefSusceptibilityChecker.check(this)

  /** This will be used solely after the [[isSusceptible]] validation
    * and therefore is safe to use in used type estimations
    */
  lazy val declaredType: SType = declaredType0.get

  /** JDT might not resolve the type based on the signature.
    * In this case, we wrap the declaring type in Option effect.
    * In order for a ref to be valid, this needs to be defined.
    */
  lazy val declaredType0: Option[SType] = {
    val typeName =
      JavaModelUtil.getResolvedTypeName(typeSignature, declaringType)
    val project = jdtElement.getJavaProject
    val declaredType = Some(project.findType(typeName, new NullProgressMonitor()))
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
