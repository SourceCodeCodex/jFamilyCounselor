package ro.lrg.jfamilycounselor.core.util.hierarchy

import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.{Flags, IType}
import ro.lrg.jfamilycounselor.core.model.`type`.Type
import ro.lrg.jfamilycounselor.core.util.cache.Cache

object ConcreteSubtypesResolver {
  private val cache = Cache[IType, List[IType]](128)

  def concreteCone(underlyingJdtObject: IType): List[IType] =
    if (cache.containsKey(underlyingJdtObject))
      cache.get(underlyingJdtObject)
    else {
      assert(!Type(underlyingJdtObject).isObjectType, "Computing the concrete cone for the Object is impossibly long.")
      val subtypes = underlyingJdtObject
        .newTypeHierarchy(new NullProgressMonitor())
        .getAllSubtypes(underlyingJdtObject)
        .toList

      val filtered = (underlyingJdtObject :: subtypes)
        .filterNot(t => t.isAnonymous)
        .filterNot(t => Flags.isInterface(t.getFlags))
        .filterNot(t => Flags.isAbstract(t.getFlags))
        .filterNot(t => Flags.isSynthetic(t.getFlags))

      cache.put(underlyingJdtObject, filtered)
      filtered
    }
}
