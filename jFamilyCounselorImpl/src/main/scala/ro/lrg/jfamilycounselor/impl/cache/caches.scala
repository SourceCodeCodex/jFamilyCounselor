package ro.lrg.jfamilycounselor.impl.cache

import org.eclipse.jdt.core.IType
import ro.lrg.jfamilycounselor.cache.Cache

private[jfamilycounselor] object ResolvedTypesByQualifiedNameCache extends Cache[String, IType]
private[jfamilycounselor] object ConcreteConeOfTypeCache extends Cache[IType, List[IType]]
