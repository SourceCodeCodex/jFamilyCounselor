package ro.lrg.jfamilycounselor.plugin;

import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IStartup;

import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;
import ro.lrg.jfamilycounselor.capability.generic.project.JavaProjectsCapability;
import ro.lrg.jfamilycounselor.util.cache.CacheSupervisor;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public final class Startup implements IStartup {
    
    private static final Logger logger = jFCLogger.getJavaLogger();

    public void earlyStartup() {
	System.setProperty("java.util.logging.SimpleFormatter.format", jFCLogger.format());
	CacheSupervisor.startMemorySupervisor();

	Factory.getInstance().setCacheCapacity(0);

	ToolRegistration.getInstance().registerXEntityConverter(element -> {

	    if (element instanceof IJavaProject iJavaProject) {
		return Factory.getInstance().createMProject(iJavaProject);
	    }

	    if (element instanceof IType iType) {
		return Factory.getInstance().createMType(iType);
	    }

	    if (element instanceof Pair<?, ?> pair) {
		if (pair._1 instanceof IType t1 && pair._2 instanceof IType t2)
		    return Factory.getInstance().createMTypesPair(new Pair<>(t1, t2));

		if (pair._1 instanceof IJavaElement r1 && pair._2 instanceof IJavaElement r2)
		    return Factory.getInstance().createMReferencesPair(new Pair<>(r1, r2));
	    }

	    return null;
	});

	ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> {
	    if (event == null || event.getDelta() == null) {
		return;
	    }

	    try {
		event.getDelta().accept(new IResourceDeltaVisitor() {
		    public boolean visit(final IResourceDelta delta) throws CoreException {
			if ((delta.getResource().getType() & IResource.PROJECT) != 0 && ((delta.getFlags() & IResourceDelta.OPEN) != 0)) {
			    logger.info("Clearing caches and reloading sources");
			    CacheSupervisor.clearCaches();
			    JavaProjectsCapability.reloadProjects();
			}
			return true;
		    }
		});
	    } catch (CoreException e) {
	    }

	});
    }

}
