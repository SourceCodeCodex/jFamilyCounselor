package ro.lrg.jfamilycounselor

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.impl.SType
import ro.lrg.jfamilycounselor.impl.pair.SConcreteTypePair

trait MConcreteTypePair {
  def jdtElement1: IType
  def jdtElement2: IType
}

object MConcreteTypePair {
  def apply(t1: IType, t2: IType): MConcreteTypePair = {
    val st1 = new SType(t1)
    val st2 = new SType(t2)
    new SConcreteTypePair(st1, st2)
  }
}
