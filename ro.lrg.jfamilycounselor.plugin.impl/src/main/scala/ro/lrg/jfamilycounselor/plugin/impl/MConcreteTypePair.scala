package ro.lrg.jfamilycounselor.plugin.impl

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.plugin.impl.model.`type`.{SConcreteTypePair, SType}

trait MConcreteTypePair {
  def jdtElement1: IType
  def jdtElement2: IType
}

object MConcreteTypePair {
  def asScala(t1: IType, t2: IType): MConcreteTypePair = {
    val st1 = SType(t1)
    val st2 = SType(t2)
    SConcreteTypePair(st1, st2)
  }
}
