package ro.lrg.jfamilycounselor.approach.relevance.typeparameter;

import static ro.lrg.jfamilycounselor.approach.relevance.typeparameter.RelevantTypeParametersUtil.relevantTypeParameters;
import static ro.lrg.jfamilycounselor.capability.project.AllTypesCapability.allTypes;
import static ro.lrg.jfamilycounselor.util.operations.CommonOperations.distrinctCombinations2;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class RelevantTypesByTypeParametersUtil {
    private RelevantTypesByTypeParametersUtil() {
    }

    public static List<IType> relevantTypes(IJavaProject iJavaProject) {
	return allTypes(iJavaProject).parallelStream().filter(RelevantTypesByTypeParametersUtil::isRelevant).toList();
    }

    public static boolean isRelevant(IType iType) {
	try {
	    return !iType.isAnonymous() &&
		    !iType.isAnnotation() &&
		    !iType.isLambda() &&
		    !iType.isRecord() &&
		    !iType.isBinary() &&
		    (iType.isClass() || iType.isInterface()) &&
		    !distrinctCombinations2(relevantTypeParameters(iType)).isEmpty();
	} catch (JavaModelException e) {
	    return false;
	}
    }

}
