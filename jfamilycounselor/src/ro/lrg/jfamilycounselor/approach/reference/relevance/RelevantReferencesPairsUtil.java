package ro.lrg.jfamilycounselor.approach.reference.relevance;

import static ro.lrg.jfamilycounselor.approach.reference.relevance.RelevantParametersUtil.relevantParameters;
import static ro.lrg.jfamilycounselor.capability.parameter.ParameterTypeCapability.parameterType;
import static ro.lrg.jfamilycounselor.capability.type.DistinctConcreteConeProductCapability.distinctConcreteConeProduct;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.distrinctCombinations2;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

/**
 * Determines the pairs of references that are relevant to be analyzed.
 * 
 * For now, there are two known types of references: parameters and `this`
 * (which can be also considered to be a parameter, yet being represented by an
 * IType object).
 * 
 * The capability promises to return two possible types of pairs:
 * 
 * - Pair<IType, ILocalVariable>
 * 
 * - Pair<ILocalVariable, ILocalVariable>
 * 
 * @author rosualinpetru
 *
 */
public class RelevantReferencesPairsUtil {
	private RelevantReferencesPairsUtil() {
	}

	private static Logger logger = jFCLogger.getLogger();

	public static List<Pair<IJavaElement, IJavaElement>> relevantReferencesPairs(IType iType) {
		return filteredParametersPairs(iType);
	}

	private static List<Pair<IJavaElement, IJavaElement>> filteredParametersPairs(IType iType) {
		var parameterPairs = distrinctCombinations2(relevantParameters(iType));
		return parameterPairs.stream().map(p -> {
			if (p._1 instanceof ILocalVariable && p._2 instanceof IType)
				return p.swap();
			else
				return p;
		}).filter(p -> {
			if (p._1 instanceof ILocalVariable p1 && p._2 instanceof ILocalVariable p2) {
				var m1 = (IMethod) p1.getDeclaringMember();
				var m2 = (IMethod) p2.getDeclaringMember();

				var t1Opt = parameterType(p1);
				var t2Opt = parameterType(p2);
				try {
					return !(m1.isConstructor() && !m2.isConstructor() || !m1.isConstructor() && m2.isConstructor()
							|| m1.isConstructor() && m2.isConstructor() && !m1.equals(m2) || t1Opt.equals(t2Opt)
							|| t1Opt.flatMap(t1 -> t2Opt.flatMap(t2 -> distinctConcreteConeProduct(t1, t2)))
									.map(cp -> cp.isEmpty()).orElse(true));
				} catch (JavaModelException e) {
					logger.warning("JavaModelException encountered: " + e.getMessage());
					return false;
				}
			}

			if (p._1 instanceof IType t && p._2 instanceof ILocalVariable param)
				return parameterType(param).flatMap(tp -> distinctConcreteConeProduct(tp, t)).map(cp -> !cp.isEmpty())
						.orElse(false);

			return true;
		})

				.toList();
	}

}
