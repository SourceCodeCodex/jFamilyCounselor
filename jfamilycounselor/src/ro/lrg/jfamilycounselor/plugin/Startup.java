package ro.lrg.jfamilycounselor.plugin;

import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.ui.IStartup;

import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;
import ro.lrg.jfamilycounselor.capability.project.JavaProjectsCapability;
import ro.lrg.jfamilycounselor.util.cache.CacheSupervisor;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public final class Startup implements IStartup {

    private static final Logger logger = jFCLogger.getLogger();

    @Override
    public void earlyStartup() {
	System.setProperty("java.util.logging.SimpleFormatter.format", jFCLogger.format());
	CacheSupervisor.startMemorySupervisor();

	Factory.getInstance().setCacheCapacity(0);
	

	// helps Insider know how to convert the implementations to the entities of the
	// metamodel
	ToolRegistration.getInstance().registerXEntityConverter(element -> {

	    if (element instanceof IJavaProject iJavaProject) {
		return Factory.getInstance().createMProject(iJavaProject);
	    }

	    if (element instanceof IType iType) {
		return Factory.getInstance().createMType(iType);
	    }

	    if (element instanceof Pair<?, ?> pair) {
		if (pair._1 instanceof IType t1 && pair._2 instanceof IType t2)
		    return Factory.getInstance().createMTypesPair(Pair.of(t1, t2));

		if (pair._1 instanceof IType r1 && pair._2 instanceof ITypeParameter r2)
		    return Factory.getInstance().createMTypeParametersPair(Pair.of(r1, r2));

		if (pair._1 instanceof ITypeParameter r1 && pair._2 instanceof ITypeParameter r2)
		    return Factory.getInstance().createMTypeParametersPair(Pair.of(r1, r2));

		if (pair._1 instanceof IType r1 && pair._2 instanceof ILocalVariable r2)
		    return Factory.getInstance().createMReferencesPair(Pair.of(r1, r2));

		if (pair._1 instanceof ILocalVariable r1 && pair._2 instanceof ILocalVariable r2)
		    return Factory.getInstance().createMReferencesPair(Pair.of(r1, r2));
	    }

	    return null;
	});

	// register a change listener to trigger the clearing of caches upon project
	// changes that could alter the results of analyses
	ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> {
	    if (event == null || event.getDelta() == null) {
		return;
	    }

	    try {
		event.getDelta().accept(new IResourceDeltaVisitor() {
		    public boolean visit(final IResourceDelta delta) throws CoreException {
			if ((delta.getResource().getType() & IResource.PROJECT) != 0 && (delta.getFlags() & IResourceDelta.OPEN) != 0 ||
				(delta.getResource().getType() & IResource.FILE) != 0 && delta.getResource().getFileExtension().contains("java") && (delta.getFlags() & IResourceDelta.CONTENT) != 0) {
			    logger.info("Clearing caches and reloading sources");
			    CacheSupervisor.clearAllCaches();
			    JavaProjectsCapability.reloadProjects();
			    return false;
			}
			return true;
		    }
		});
	    } catch (CoreException e) {
	    }

	});
	
	var log = Platform.getLog(getClass());
	log.info("jFamilyCounselor has started!");
	
    }

}
