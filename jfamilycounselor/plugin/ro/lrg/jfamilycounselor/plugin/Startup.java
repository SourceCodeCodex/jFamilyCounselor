package ro.lrg.jfamilycounselor.plugin;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IStartup;

import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;
import ro.lrg.jfamilycounselor.util.cache.CacheManager;
import ro.lrg.jfamilycounselor.util.datatype.Pair;
import ro.lrg.jfamilycounselor.util.logging.jFCLogger;

public final class Startup implements IStartup {

    public void earlyStartup() {
	System.setProperty("java.util.logging.SimpleFormatter.format", jFCLogger.format());
	CacheManager.startMemorySupervisor();
	
	Factory.getInstance().setCacheCapacity(0);
	
	ToolRegistration.getInstance().registerXEntityConverter(element -> {

	    if (element instanceof IJavaProject iJavaProject) {
		return Factory.getInstance().createMProject(iJavaProject);
	    }
	    
	    if (element instanceof IType iType) {
		return Factory.getInstance().createMType(iType);
	    }
	    
	    if (element instanceof Pair<?, ?> pair) {
		if(pair._1 instanceof IType t1 && pair._2 instanceof IType t2)
		    return Factory.getInstance().createMTypesPair(new Pair<>(t1, t2));
		
		if(pair._1 instanceof IJavaElement r1 && pair._2 instanceof IJavaElement r2)
		    return Factory.getInstance().createMReferencesPair(new Pair<>(r1, r2));
	    }
	    
	    return null;
	});
    }

}
