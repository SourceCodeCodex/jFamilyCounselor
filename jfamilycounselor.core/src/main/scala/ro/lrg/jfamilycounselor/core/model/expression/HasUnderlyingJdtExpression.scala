package ro.lrg.jfamilycounselor.core.model.expression

import org.eclipse.jdt.core.{IType, dom}
import ro.lrg.jfamilycounselor.core.model.`type`.Type

trait HasUnderlyingJdtExpression {
  def underlyingJdtExpression: dom.Expression

  def `type`: Option[Type] = for {
    binding <- Option(underlyingJdtExpression.resolveTypeBinding())
    underlyingJavaObject <- Option(binding.getJavaElement)
    if underlyingJavaObject.isInstanceOf[IType]
  } yield Type(underlyingJavaObject.asInstanceOf[IType])
}
