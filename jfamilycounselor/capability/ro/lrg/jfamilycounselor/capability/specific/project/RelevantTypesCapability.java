package ro.lrg.jfamilycounselor.capability.specific.project;

import static ro.lrg.jfamilycounselor.capability.generic.project.AllTypesCapability.allTypes;
import static ro.lrg.jfamilycounselor.capability.specific.reference.RelevantParametersCapability.relevantParameters;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class RelevantTypesCapability {
    private RelevantTypesCapability() {
    }

    public static List<IType> relevantTypes(IJavaProject iJavaProject) {
	return allTypes(iJavaProject).parallelStream().filter(RelevantTypesCapability::isRelevant).toList();
    }

    public static boolean isRelevant(IType iType) {
	try {
	    return !iType.isAnonymous() &&
		    !iType.isAnnotation() &&
		    !iType.isLambda() &&
		    !iType.isRecord() &&
		    !iType.isBinary() &&
		    (iType.isClass() || iType.isInterface()) &&
		    iType.getTypeParameters().length == 0 &&
		    relevantParameters(iType).size() >= 2;
	} catch (JavaModelException e) {
	    return false;
	}
    }

}
