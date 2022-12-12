package ro.lrg.jfamilycounselor.core.model.project

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.core.model.`type`.Type

final case class Project(underlyingJdtObject: IJavaProject) {
  lazy val relevantClasses: List[Type] =
    types.filter(_.isRelevant)

  lazy val types: List[Type] = for {
    fragment <- underlyingJdtObject.getPackageFragments.toList
    compilationUnit <- fragment.getCompilationUnits.toList
    tpe <- compilationUnit.getTypes.map(Type)
  } yield tpe

  override def toString: String = underlyingJdtObject.getElementName + ": Project"
}


