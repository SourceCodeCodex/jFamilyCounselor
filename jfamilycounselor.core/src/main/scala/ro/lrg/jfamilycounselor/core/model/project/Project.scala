package ro.lrg.jfamilycounselor.core.model.project

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.core.model.`type`.Type

import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

final case class Project(underlyingJdtObject: IJavaProject) {
  lazy val relevantClasses: List[Type] =
    types.par.filter(_.isRelevant).toList

  lazy val types: List[Type] = for {
    fragment <- underlyingJdtObject.getPackageFragments.toList
    compilationUnit <- fragment.getCompilationUnits.toList
    tpe <- compilationUnit.getTypes.map(Type)
  } yield tpe

  override lazy val toString: String = underlyingJdtObject.getElementName
}


