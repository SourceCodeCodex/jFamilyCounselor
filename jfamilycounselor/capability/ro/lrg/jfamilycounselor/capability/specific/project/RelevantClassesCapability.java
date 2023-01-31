package ro.lrg.jfamilycounselor.capability.specific.project;

import static ro.lrg.jfamilycounselor.capability.specific.reference.ReferencesPairsCapability.relevantReferencesPairs;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class RelevantClassesCapability {
    private RelevantClassesCapability() {
    }

    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<IType> relevantClasses(IJavaProject iJavaProject) {
	try {
	    return Arrays.asList(iJavaProject.getPackageFragments())
		    .parallelStream()
		    .flatMap(pf -> {
			try {
			    return Arrays.asList(pf.getCompilationUnits())
				    .stream()
				    .flatMap(cu -> {
					try {
					    return Arrays.asList(cu.getTypes()).stream();
					} catch (JavaModelException e) {
					    logger.warning("JavaModelException encountered: " + e.getMessage());
					    return Stream.empty();
					}
				    })
				    .filter(t -> isRelevant(t));
			} catch (JavaModelException e) {
			    logger.warning("JavaModelException encountered: " + e.getMessage());
			    return Stream.empty();
			}

		    }).toList();

	} catch (JavaModelException e) {
	    logger.warning("JavaModelException encountered: " + e.getMessage());
	    return List.of();
	}
    }

    private static boolean isRelevant(IType iType) {
	try {
	    return !iType.isAnonymous() &&
		    !iType.isAnnotation() &&
		    !iType.isLambda() &&
		    !iType.isRecord() &&
		    !iType.isBinary() &&
		    iType.getTypeParameters().length == 0 &&
		    !relevantReferencesPairs(iType).isEmpty();
	} catch (JavaModelException e) {
	    return false;
	}
    }

}
