package ro.lrg.jfamilycounselor.approach.reference.relevance;

import static ro.lrg.jfamilycounselor.approach.reference.relevance.RelevantReferencesPairsUtil.relevantReferencesPairs;
import static ro.lrg.jfamilycounselor.capability.project.AllTypesCapability.allTypes;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class RelevantTypesByReferencesUtil {
	private RelevantTypesByReferencesUtil() {
	}

	public static List<IType> relevantTypes(IJavaProject iJavaProject) {
		return allTypes(iJavaProject).parallelStream().filter(RelevantTypesByReferencesUtil::isRelevant).toList();
	}

	public static boolean isRelevant(IType iType) {
		try {
			return !iType.isAnonymous() && !iType.isAnnotation() && !iType.isLambda() && !iType.isRecord()
					&& !iType.isBinary() && (iType.isClass() || iType.isInterface())
					&& !relevantReferencesPairs(iType).isEmpty();
		} catch (JavaModelException e) {
			return false;
		}
	}

}
