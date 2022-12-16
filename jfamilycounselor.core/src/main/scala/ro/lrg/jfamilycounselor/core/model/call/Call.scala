package ro.lrg.jfamilycounselor.core.model.call


import org.eclipse.jdt.core.IMethod
import org.eclipse.jdt.core.dom
import ro.lrg.jfamilycounselor.core.model.expression.Expression
import ro.lrg.jfamilycounselor.core.model.method.Method

import scala.jdk.CollectionConverters.ListHasAsScala

sealed trait Call {
  type UnderlyingJdtObjectType <: dom.Expression

  def underlyingJdtObject: UnderlyingJdtObjectType

  override def toString: String = underlyingJdtObject.toString

  def argument(index: Int): Expression

  def method: Option[Method]

  def methodUnsafe: Method = method.get
}

final case class Instantiation(underlyingJdtObject: dom.ClassInstanceCreation) extends Call {
  override type UnderlyingJdtObjectType = dom.ClassInstanceCreation

  override def argument(index: Int): Expression =
    Expression(underlyingJdtObject.arguments().asScala.toList.asInstanceOf[List[dom.Expression]](index))

  override lazy val method: Option[Method] = Option(underlyingJdtObject.resolveConstructorBinding())
    .map(_.getJavaElement.asInstanceOf[IMethod])
    .map(Method.apply)
}

final case class MethodCall(underlyingJdtObject: dom.MethodInvocation) extends Call {
  override type UnderlyingJdtObjectType = dom.MethodInvocation

  def callExpression: Option[Expression] = Option(Expression(underlyingJdtObject.getExpression))

  override def argument(index: Int): Expression =
    Expression(underlyingJdtObject.arguments().asScala.toList.asInstanceOf[List[dom.Expression]](index))

  override lazy val method: Option[Method] = Option(underlyingJdtObject.resolveMethodBinding())
    .map(_.getJavaElement.asInstanceOf[IMethod])
    .map(Method.apply)
}

final case class SuperMethodCall(underlyingJdtObject: dom.SuperMethodInvocation) extends Call {
  override type UnderlyingJdtObjectType = dom.SuperMethodInvocation

  override def argument(index: Int): Expression =
    Expression(underlyingJdtObject.arguments().asScala.toList.asInstanceOf[List[dom.Expression]](index))

  override lazy val method: Option[Method] = Option(underlyingJdtObject.resolveMethodBinding())
    .map(_.getJavaElement.asInstanceOf[IMethod])
    .map(Method.apply)
}
