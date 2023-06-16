package ro.lrg.jfamilycounselor.approach.relevance;

import static ro.lrg.jfamilycounselor.approach.relevance.RelevantReferencesPairsCapability.relevantReferencesPairs;
import static ro.lrg.jfamilycounselor.capability.project.AllTypesCapability.allTypes;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Capability that filters the relevant types of a Java project, i.e. types that
 * are worth to be analyzed by any of the available anaylses.
 * 
 * @author rosualinpetru
 *
 */
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
		    relevantReferencesPairs(iType).size() >= 2;
	} catch (JavaModelException e) {
	    return false;
	}
    }

}
