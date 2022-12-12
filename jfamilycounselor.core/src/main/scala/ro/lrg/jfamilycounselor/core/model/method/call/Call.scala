package ro.lrg.jfamilycounselor.core.model.method.call

import org.eclipse.jdt.core.dom._

import scala.jdk.CollectionConverters.ListHasAsScala

sealed trait Call {
  type UnderlyingJdtObjectType <: ASTNode

  def underlyingJdtObject: UnderlyingJdtObjectType

  override def toString: String = underlyingJdtObject.toString

  def argAtIndex(index: Int): Expression

  def isCalledOnSameObjectWith(call: Call): Boolean
}

final case class Instantiation(underlyingJdtObject: ClassInstanceCreation) extends Call {
  override type UnderlyingJdtObjectType = ClassInstanceCreation

  override def argAtIndex(index: Int): Expression =
    underlyingJdtObject.arguments().asScala.toList.asInstanceOf[List[Expression]](index)

  override def isCalledOnSameObjectWith(call: Call): Boolean = this == call
}

final case class MethodCall(underlyingJdtObject: MethodInvocation) extends Call {
  override type UnderlyingJdtObjectType = MethodInvocation

  val callExpression: Option[Expression] = Option(underlyingJdtObject.getExpression)

  def isCalledOnSameObjectWith(call: Call): Boolean = call match {
    case _: Instantiation => false
    case m: MethodCall =>
      (callExpression, m.callExpression) match {
        case (None, None) => true
        case (Some(n1: SimpleName), Some(n2: SimpleName)) =>
          Option(n1.resolveBinding()).flatMap(b => Option(b.getJavaElement)) ==
            Option(n2.resolveBinding()).flatMap(b => Option(b.getJavaElement))
        case _ => false
      }
    case _: SuperMethodCall => callExpression.isEmpty || callExpression.get.getNodeType == ASTNode.THIS_EXPRESSION
  }

  override def argAtIndex(index: Int): Expression =
    underlyingJdtObject.arguments().asScala.toList.asInstanceOf[List[Expression]](index)
}

final case class SuperMethodCall(underlyingJdtObject: SuperMethodInvocation)
  extends Call {
  override type UnderlyingJdtObjectType = SuperMethodInvocation

  override def argAtIndex(index: Int): Expression =
    underlyingJdtObject.arguments().asScala.toList.asInstanceOf[List[Expression]](index)

  override def isCalledOnSameObjectWith(call: Call): Boolean = call match {
    case _: Instantiation => false
    case m: MethodCall => m.callExpression.isEmpty || m.callExpression.get.getNodeType == ASTNode.THIS_EXPRESSION
    case _: SuperMethodCall => true
  }
}
