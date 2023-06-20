package ro.lrg.jfamilycounselor.capability.typeparameter;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

@SuppressWarnings("restriction")
public class TypeParameterBoundsTypesCapability {
    private TypeParameterBoundsTypesCapability() {
    }

    private static final Cache<ITypeParameter, List<IType>> cache = MonitoredUnboundedCache.getLowConsumingCache();

    private static final Logger logger = jFCLogger.getLogger();

    public static Optional<List<IType>> typeParameterBoundsTypes(ITypeParameter iTypeParameter) {
	if (cache.contains(iTypeParameter))
	    return cache.get(iTypeParameter);

	IType declaringType;
	if (iTypeParameter.getDeclaringMember() instanceof IType iType)
	    declaringType = iType;
	else if (iTypeParameter.getDeclaringMember() instanceof IMethod iMethod)
	    declaringType = iMethod.getDeclaringType();
	else
	    return Optional.empty();

	if (declaringType == null)
	    return Optional.empty();

	Optional<List<String>> resolvedTypesNames = Optional.empty();
	try {
	    var bounds = List.of(iTypeParameter.getBoundsSignatures());
	    var aux = bounds.stream()
		    .map(boundSignature -> {
			try {
			    return Optional.ofNullable(JavaModelUtil.getResolvedTypeName(boundSignature, declaringType));
			} catch (JavaModelException e) {
			    logger.warning("JavaModelException encountered: " + e.getMessage());
			    return Optional.<String>empty();
			}
		    })
		    .toList();

	    if (aux.stream().anyMatch(o -> o.isEmpty()))
		resolvedTypesNames = Optional.empty();
	    else
		resolvedTypesNames = Optional.of(aux.stream().map(o -> o.get()).toList());

	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return Optional.empty();
	}

	resolvedTypesNames = resolvedTypesNames.map(l -> l.stream().map(s -> s.replace('/', '.')).toList());

	var project = Optional.ofNullable(iTypeParameter.getJavaProject());

	var iTypesOpt = resolvedTypesNames.flatMap(typesNames -> project.map(p -> typesNames.stream()
		.map(typeName -> {
		    try {
			return Optional.ofNullable(p.findType(typeName, new NullProgressMonitor()));
		    } catch (JavaModelException e) {
			logger.warning("JavaModelException encountered: " + e.getMessage());
			return Optional.<IType>empty();
		    }

		}).toList()));

	if (iTypesOpt.stream().anyMatch(l -> l.stream().anyMatch(o -> o.isEmpty())))
	    return Optional.empty();

	var iTypes = iTypesOpt.get().stream().map(o -> o.get()).toList();
	cache.put(iTypeParameter, iTypes);

	return Optional.of(iTypes);
    }
}
