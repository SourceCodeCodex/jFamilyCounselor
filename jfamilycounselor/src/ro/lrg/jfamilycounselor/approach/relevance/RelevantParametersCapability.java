package ro.lrg.jfamilycounselor.approach.relevance;

import static ro.lrg.jfamilycounselor.capability.parameter.ParameterTypeCapability.parameterType;
import static ro.lrg.jfamilycounselor.capability.type.ConcreteConeCapability.concreteCone;

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

import ro.lrg.jfamilycounselor.Constants;
import ro.lrg.jfamilycounselor.util.cache.Cache;
import ro.lrg.jfamilycounselor.util.cache.MonitoredUnboundedCache;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Capability that computes the list of all relevant parameters, regardless of
 * the method/constructor they are declared in. This also considers the 'this'
 * reference as a parameter. 'this' is represented by its correspondent IType
 * object.
 * 
 * @author rosualinpetru
 *
 */
public class RelevantParametersCapability {
    private RelevantParametersCapability() {
    }

    private static Cache<IJavaElement, Boolean> cache = MonitoredUnboundedCache.getLowConsumingCache();

    private static Logger logger = jFCLogger.getLogger();

    public static List<IJavaElement> relevantParameters(IType iType) {
	try {
	    var result = new ArrayList<IJavaElement>();

	    // 'this' parameter
	    if (isRelevant(iType))
		result.add(iType);

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

	    return result;
	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return List.of();
	}
    }

    // parameter relevance
    private static boolean isRelevant(ILocalVariable iLocalVariable, IType declaringType) {
	if (cache.contains(iLocalVariable))
	    return cache.get(iLocalVariable).get();

	var iType = parameterType(iLocalVariable);

	if (iType.isEmpty()) {
	    cache.put(iLocalVariable, false);
	    return false;
	} else {
	    var t = iType.get();

	    try {
		var result = !t.getFullyQualifiedName().equals(Constants.OBJECT_FQN) &&
			t.getCompilationUnit() != null &&
			!t.isAnonymous() &&
			!t.isLambda() &&
			(t.isClass() || t.isInterface()) &&
			Arrays.asList(t.getTypeParameters()).isEmpty() &&
			!t.getFullyQualifiedName().equals(declaringType.getFullyQualifiedName()) &&
			concreteCone(t).stream().anyMatch(cone -> cone.size() >= 2) &&
			Signature.getTypeSignatureKind(iLocalVariable.getTypeSignature()) != Signature.ARRAY_TYPE_SIGNATURE;

		cache.put(iLocalVariable, result);
		return result;
	    } catch (Throwable e) {
		cache.put(iLocalVariable, false);
		return false;
	    }

	}
    }

    // 'this' relevance
    private static boolean isRelevant(IType t) {
	if (cache.contains(t))
	    return cache.get(t).get();

	try {
	    var result = t.getCompilationUnit() != null &&
		    !t.isAnonymous() &&
		    !t.isLambda() &&
		    (t.isClass() || t.isInterface()) &&
		    Arrays.asList(t.getTypeParameters()).isEmpty() &&
		    concreteCone(t).stream().anyMatch(cone -> cone.size() >= 2);

	    cache.put(t, result);
	    return result;
	} catch (Throwable e) {
	    cache.put(t, false);
	    return false;
	}

    }

}
