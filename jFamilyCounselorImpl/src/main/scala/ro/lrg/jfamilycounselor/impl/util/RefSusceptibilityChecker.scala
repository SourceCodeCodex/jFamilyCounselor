package ro.lrg.jfamilycounselor.impl.util

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.impl.SType
import ro.lrg.jfamilycounselor.impl.ref.SRef

object RefSusceptibilityChecker {
  private val susTypes: scala.collection.mutable.Set[IType] =
    scala.collection.mutable.Set()

  def check(r: SRef): Boolean = {
    def internal(sType: SType): Boolean = {
      val jdtType = sType.jdtElement
      jdtType.getCompilationUnit != null &&
      !jdtType.isAnonymous &&
      (jdtType.isClass || jdtType.isInterface) &&
      jdtType.getTypeParameters.isEmpty &&
      sType.concreteCone.size >= 2
    }

    r.declaredType0.exists(tpe =>
      susTypes.contains(tpe.jdtElement) ||
        (internal(tpe) && susTypes.add(tpe.jdtElement))
    )
  }

}
