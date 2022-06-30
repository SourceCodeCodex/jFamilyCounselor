package ro.lrg.jfamilycounselor.model.ref

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core._
import org.eclipse.jdt.internal.corext.util.JavaModelUtil
import ro.lrg.jfamilycounselor.model.`type`.SType
import ro.lrg.jfamilycounselor.model.method.SMethod

sealed abstract class SRef {
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
    import ro.lrg.jfamilycounselor.cache.implicits._

    val typeName: Option[String] = Some(
      JavaModelUtil.getResolvedTypeName(typeSignature, declaringType)
    ).filter(_ != null)

    def searchTypeByFQN(fqn: String) =
      jdtElement.getJavaProject.findType(fqn, new NullProgressMonitor())

    val declaredType =
      typeName
        .map { name => searchTypeByFQN(name).cachedBy(name) }
        .filter(_ != null)

    declaredType.map(SType)
  }
  protected def typeSignature: String
  protected def declaringType: IType
}

final case class SParam(param: ILocalVariable) extends SRef {

  override type jdtType = ILocalVariable
  override val jdtElement: jdtType = param

  def declaringMethod: SMethod = new SMethod(
    param.getDeclaringMember.asInstanceOf[IMethod]
  )

  override lazy val isSusceptible: Boolean = super.isSusceptible

  override protected def typeSignature: String = param.getTypeSignature

  override protected def declaringType: IType =
    param.getDeclaringMember.getDeclaringType

  override def toString: String = s"$declaringMethod / ${param.getElementName}"
}
final case class SField(field: IField) extends SRef {

  override type jdtType = IField
  override val jdtElement: jdtType = field

  override lazy val isSusceptible: Boolean = super.isSusceptible &&
    !Flags.isStatic(field.getFlags)

  override protected def typeSignature: String = field.getTypeSignature

  override protected def declaringType: IType = field.getDeclaringType

  override def toString: String = field.getElementName
}
