package ro.lrg.jfamilycounselor.capability.generic.parameter;

import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Service that attempts to determine the type of a particular parameter. This
 * service makes use of the signature of the parameter and the project in order
 * to find its type's IType object. As an alternative, one could use the
 * ASTParser.createBindings(...) method.
 * 
 * @author rosualinpetru
 *
 */

@SuppressWarnings("restriction")
public class ParameterTypeCapability {
    private ParameterTypeCapability() {
    }

    private static final Cache<ILocalVariable, IType> cache = MonitoredUnboundedCache.getCache();

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static Optional<IType> parameterType(ILocalVariable iLocalVariable) {
	if (iLocalVariable.isParameter() && iLocalVariable.getDeclaringMember() instanceof IMethod iMethod) {
	    if (cache.contains(iLocalVariable))
		return cache.get(iLocalVariable);

	    Optional<String> resolvedTypeName = Optional.empty();
	    try {
		resolvedTypeName = Optional.ofNullable(JavaModelUtil.getResolvedTypeName(iLocalVariable.getTypeSignature(), iMethod.getDeclaringType()));
	    } catch (JavaModelException e) {
		logger.warning("JavaModelException encountered: " + e.getMessage());
		return Optional.empty();
	    }

	    resolvedTypeName = resolvedTypeName.map(s -> s.replace('/', '.'));

	    var iType = resolvedTypeName.flatMap(typeName -> {
		var project = Optional.ofNullable(iLocalVariable.getJavaProject());

		return project.flatMap(p -> {
		    try {
			return Optional.ofNullable(p.findType(typeName, new NullProgressMonitor()));
		    } catch (JavaModelException e) {
			logger.warning("JavaModelException encountered: " + e.getMessage());
			return Optional.empty();
		    }
		});
	    });

	    if (iType.isPresent())
		cache.put(iLocalVariable, iType.get());

	    return iType;
	}

	return Optional.empty();
    }
}
