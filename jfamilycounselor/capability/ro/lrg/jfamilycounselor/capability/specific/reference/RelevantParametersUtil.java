package ro.lrg.jfamilycounselor.capability.specific.reference;

import static ro.lrg.jfamilycounselor.capability.generic.cone.ConcreteConeCapability.concreteCone;
import static ro.lrg.jfamilycounselor.capability.generic.resolver.ParameterTypeCapability.parameterType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import ro.lrg.jfamilycounselor.util.Constants;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.CacheService;
import ro.lrg.jfamilycounselor.util.cache.KeyManager;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Service responsible for computing the list of all parameters, regardless of
 * the method/constructor they are declared in. This service also considers the
 * 'this' reference as a parameter. 'this' is represented by its correspondent
 * IType object.
 * 
 * @author rosualinpetru
 *
 */
class RelevantParametersUtil {
    private RelevantParametersUtil() {
    }

    private static Cache<String, Boolean> cache = CacheService.getCache(8192);

    private static Logger logger = jFCLogger.getJavaLogger();

    public static List<IJavaElement> relevantParameters(IType iType) {
	try {
	    var result = new ArrayList<IJavaElement>();

	    // add the rest of the parameters
	    var iMethods = Arrays.asList(iType.getMethods());
	    iMethods
		    .stream()
		    .filter(m -> {
			try {
			    return !Flags.isStatic(m.getFlags());
			} catch (Throwable e) {
			    return false;
			}
		    })
		    .flatMap(m -> {
			try {
			    return Arrays.asList(m.getParameters()).stream();
			} catch (JavaModelException e) {
			    return Stream.of();
			}
		    })
		    .filter(p -> isRelevant(p, iType))
		    .toList()
		    .forEach(p -> result.add(p));

	    // 'this' parameter
	    result.add(0, iType);

	    return result;
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return List.of();
	}
    }

    private static boolean isRelevant(ILocalVariable iLocalVariable, IType declaringType) {
	var key = KeyManager.parameter(iLocalVariable);

	var iType = parameterType(iLocalVariable);

	if (iType.isEmpty()) {
	    cache.put(key, false);
	    return false;
	} else {
	    var t = iType.get();

	    try {
		var result = !t.getFullyQualifiedName().equals(Constants.OBJECT_FQN) &&
			t.getCompilationUnit() != null &&
			!t.isAnonymous() &&
			(t.isClass() || t.isInterface()) &&
			Arrays.asList(t.getTypeParameters()).isEmpty() &&
			!t.getFullyQualifiedName().equals(declaringType.getFullyQualifiedName()) &&
			concreteCone(t).stream().anyMatch(cone -> cone.size() >= 2) && 
			Signature.getTypeSignatureKind(iLocalVariable.getTypeSignature()) != Signature.ARRAY_TYPE_SIGNATURE;

		cache.put(key, result);
		return result;
	    } catch (Throwable e) {
		cache.put(key, false);
		return false;
	    }

	}
    }

}
