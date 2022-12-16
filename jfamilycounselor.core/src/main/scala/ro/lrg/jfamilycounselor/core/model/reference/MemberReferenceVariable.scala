package ro.lrg.jfamilycounselor.core.model.reference

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.IJavaElement
import org.eclipse.jdt.internal.corext.util.JavaModelUtil
import ro.lrg.jfamilycounselor.core.model.`type`.Type

/**
  * This is a convenient way to modularize code to keep compilation units small while using
  * sealed traits.
  */
private[reference] abstract class MemberReferenceVariable[T <: IJavaElement](underlyingJdtObject: T) {

  /** Tells whether a member reference variable is worth being taken into
    * consideration for the analysis.
    */
  def isRelevant: Boolean =
    `type`
      .exists(`type` =>
        Option(`type`.underlyingJdtObject.getCompilationUnit).isDefined &&
          !`type`.underlyingJdtObject.isAnonymous &&
          (`type`.underlyingJdtObject.isClass || `type`.underlyingJdtObject.isInterface) &&
          `type`.underlyingJdtObject.getTypeParameters.isEmpty &&
          `type`.concreteCone.size >= 2
      )

  lazy val `type`: Option[Type] =
    for {
      typeName <- Option(
        JavaModelUtil.getResolvedTypeName(
          typeSignature,
          declaringType.underlyingJdtObject
        )
      )
        .map(_.replace('/', '.'))
      jdtType <- Option(
        underlyingJdtObject.getJavaProject
          .findType(typeName, new NullProgressMonitor())
      )
    } yield Type(jdtType)

  /** The type with which a member reference is declared.
    * In case of parameters, the declaring type is de declaring
    * type of the method the parameter is declared in.
    */
  def declaringType: Type

  protected def typeSignature: String
}

