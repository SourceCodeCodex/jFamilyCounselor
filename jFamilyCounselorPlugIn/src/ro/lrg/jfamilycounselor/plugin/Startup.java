package ro.lrg.jfamilycounselor.plugin;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IStartup;

import jfamilycounselorplugin.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;
import ro.lrg.jfamilycounselor.MProject;
import ro.lrg.jfamilycounselor.MType;

public final class Startup implements IStartup {

    @Override
    public void earlyStartup() {
	ToolRegistration.getInstance().registerXEntityConverter(element -> {
	    if (element instanceof IJavaProject project) {
		return Factory.getInstance().createMProject(MProject.apply(project));
	    }
	    if (element instanceof IType type) {
		return Factory.getInstance().createMType(MType.apply(type));
	    }

	    if (element instanceof ro.lrg.jfamilycounselor.MProject mProject) {
		return Factory.getInstance().createMProject(mProject);
	    }
	    if (element instanceof ro.lrg.jfamilycounselor.MType mType) {
		return Factory.getInstance().createMType(mType);
	    }
	    if (element instanceof ro.lrg.jfamilycounselor.MRefPair mRefPair) {
		return Factory.getInstance().createMRefPair(mRefPair);
	    }
	    if (element instanceof ro.lrg.jfamilycounselor.MConcreteTypePair mTypePair) {
		return Factory.getInstance().createMConcreteTypePair(mTypePair);
	    }

	    return null;
	});
    }

}
