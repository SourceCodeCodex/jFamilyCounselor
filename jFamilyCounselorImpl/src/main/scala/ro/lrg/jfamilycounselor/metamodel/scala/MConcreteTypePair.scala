package ro.lrg.jfamilycounselor.metamodel.scala

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.model.`type`.{SConcreteTypePair, SType}

trait MConcreteTypePair {
  def jdtElement1: IType
  def jdtElement2: IType
}

object MConcreteTypePair {
  def asScala(t1: IType, t2: IType): MConcreteTypePair = {
    val st1 = new SType(t1)
    val st2 = new SType(t2)
    new SConcreteTypePair(st1, st2)
  }
}
