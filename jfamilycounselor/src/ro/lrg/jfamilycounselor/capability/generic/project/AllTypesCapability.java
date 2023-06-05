package ro.lrg.jfamilycounselor.capability.generic.project;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public class AllTypesCapability {
    private AllTypesCapability() {
    }
    
    private static final Logger logger = jFCLogger.getJavaLogger();

    public static List<IType> allTypes(IJavaProject iJavaProject) {
	try {
	    return Arrays.asList(iJavaProject.getPackageFragments())
		    .stream()
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
				    });
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

}