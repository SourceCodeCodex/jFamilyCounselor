package ro.lrg.jfamilycounselor.core.model.project

import org.eclipse.jdt.core.IJavaProject
import ro.lrg.jfamilycounselor.core.model.`type`.Type

import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable

final case class Project(underlyingJdtObject: IJavaProject) {
  def relevantClasses: List[Type] =
    types.par.filter(_.isRelevant).toList

  def types: List[Type] = iTypes.map(Type)

  private lazy val iTypes = for {
    fragment <- underlyingJdtObject.getPackageFragments.toList
    compilationUnit <- fragment.getCompilationUnits.toList
    tpe <- compilationUnit.getTypes
  } yield tpe

  override def toString: String = underlyingJdtObject.getElementName
}


