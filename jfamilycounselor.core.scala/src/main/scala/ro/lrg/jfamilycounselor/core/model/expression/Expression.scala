package ro.lrg.jfamilycounselor.core.model.expression

import org.eclipse.jdt.core.{IType, dom}
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.model.call.{Call, Instantiation, MethodCall, SuperMethodCall}
import ro.lrg.jfamilycounselor.core.model.reference._

sealed trait Expression {
  def `type`: Option[Type]
}

object Expression {

  import ExpressionTypeUtil._

  def apply(underlyingJdtObject: dom.Expression): Expression = underlyingJdtObject match {
    case null => UnknownExpression(None)
    case e: dom.ParenthesizedExpression => ParenthesizedExpression(e)
    case e: dom.Assignment => AssignmentExpression(e)
    case e: dom.CastExpression => CastExpression(e)
    case e: dom.ConditionalExpression => ConditionalExpression(e)
    case e: dom.ThisExpression if jdtType(e).isDefined => ThisExpression(This(jdtType(e).get))
    case e: dom.SimpleName if isField(e) => FieldExpression(Field(jdtElement(e).get))
    case e: dom.FieldAccess if jdtElement(e).isDefined => FieldExpression(Field(jdtElement(e).get))
    case e: dom.SuperFieldAccess if jdtElement(e).isDefined => FieldExpression(Field(jdtElement(e).get))
    case e: dom.SimpleName if isParameter(e) => ParameterExpression(Parameter(jdtElement(e).get))
    case e: dom.SimpleName if isLocalVariable(e) => LocalVariableExpression(LocalVariable(jdtElement(e).get))
    case e: dom.ClassInstanceCreation => CallExpression(Instantiation(e))
    case e: dom.MethodInvocation => CallExpression(MethodCall(e))
    case e: dom.SuperMethodInvocation => CallExpression(SuperMethodCall(e))
    case e => UnknownExpression(Some(e))
  }

}

case class ThisExpression(referenceVariable: This) extends Expression with HasReferenceVariable

case class LocalVariableExpression private(referenceVariable: LocalVariable) extends Expression with HasReferenceVariable

case class ParameterExpression private(referenceVariable: Parameter) extends Expression with HasReferenceVariable

case class FieldExpression(referenceVariable: Field) extends Expression with HasReferenceVariable

case class AssignmentExpression(underlyingJdtExpression: dom.Assignment) extends Expression with HasUnderlyingJdtExpression

case class CastExpression(underlyingJdtExpression: dom.CastExpression) extends Expression with HasUnderlyingJdtExpression

case class ConditionalExpression(underlyingJdtExpression: dom.ConditionalExpression) extends Expression with HasUnderlyingJdtExpression

case class ParenthesizedExpression(underlyingJdtExpression: dom.ParenthesizedExpression) extends Expression with HasUnderlyingJdtExpression

case class CallExpression(call: Call) extends Expression with HasUnderlyingJdtExpression {
  override def underlyingJdtExpression: dom.Expression = call.underlyingJdtObject
}

case class UnknownExpression(underlyingJdtExpression: Option[dom.Expression]) extends Expression {
  override lazy val `type`: Option[Type] = for {
    exp <- underlyingJdtExpression
    binding <- Option(exp.resolveTypeBinding())
    underlyingJavaObject <- Option(binding.getJavaElement)
    if underlyingJavaObject.isInstanceOf[IType]
  } yield Type(underlyingJavaObject.asInstanceOf[IType])
}