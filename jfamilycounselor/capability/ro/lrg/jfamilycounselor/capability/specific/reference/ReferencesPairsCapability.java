package ro.lrg.jfamilycounselor.capability.specific.reference;

import static ro.lrg.jfamilycounselor.util.list.CommonOperations.distrinctCombinations2;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.capability.generic.type.ParameterTypeCapability;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Service that determines the pairs of references that are relevant to be
 * analyzed. This represents the variability point for possibly future types of
 * references that might be analyzed (e.g. fields).
 * 
 * For now, there are two known types of references: parameters and `this`
 * (which can be also considered a parameter, yet is represented by an IType
 * object).
 * 
 * The service promises to return two possible types of pairs:
 * 
 * - Pair<IType, ILocalVariable>
 * 
 * - Pair<ILocalVariable, ILocalVariable>
 * 
 * @author rosualinpetru
 *
 */
public class ReferencesPairsCapability {
    private ReferencesPairsCapability() {
    }

    private static Logger logger = jFCLogger.getJavaLogger();

    public static List<Pair<IJavaElement, IJavaElement>> relevantReferencesPairs(IType iType) {
	return filteredParametersPairs(iType);
    }

    private static List<Pair<IJavaElement, IJavaElement>> filteredParametersPairs(IType iType) {
	var parameterPairs = distrinctCombinations2(RelevantParametersCapability.relevantParameters(iType));
	return parameterPairs
		.stream()
		.filter(p -> {
		    if (p._1 instanceof ILocalVariable p1 && p._2 instanceof ILocalVariable p2) {
			var m1 = (IMethod) p1.getDeclaringMember();
			var m2 = (IMethod) p2.getDeclaringMember();

			var t1 = ParameterTypeCapability.parameterType(p1);
			var t2 = ParameterTypeCapability.parameterType(p2);
			try {
			    return !(m1.isConstructor() && !m2.isConstructor() ||
				    !m1.isConstructor() && m2.isConstructor() ||
				    m1.isConstructor() && m2.isConstructor() && !m1.equals(m2) ||
				    t1.equals(t2));
			} catch (JavaModelException e) {
			    logger.warning("JavaModelException encountered: " + e.getMessage());
			    return false;
			}
		    }

		    return true;
		})
		.map(p -> {
		    if (p._1 instanceof ILocalVariable && p._2 instanceof IType)
			return p.swap();
		    else
			return p;
		})
		.toList();
    }

}
