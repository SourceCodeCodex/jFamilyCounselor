package ro.lrg.jfamilycounselor.capability.generic.resolver;

import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.CacheManager;
import ro.lrg.jfamilycounselor.util.cache.KeyManager;
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

    private static final Cache<String, IType> cache = CacheManager.getCache(2048);

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static Optional<IType> parameterType(ILocalVariable iLocalVariable) {
	if (iLocalVariable.isParameter() && iLocalVariable.getDeclaringMember() instanceof IMethod iMethod) {
	    var key = KeyManager.parameter(iLocalVariable);

	    if (cache.contains(key))
		return cache.get(key);

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
			return Optional.ofNullable(p.findType(typeName));
		    } catch (JavaModelException e) {
			logger.warning("JavaModelException encountered: " + e.getMessage());
			return Optional.empty();
		    }
		});
	    });

	    if (iType.isPresent())
		cache.put(key, iType.get());

	    return iType;
	}

	return Optional.empty();
    }
}
