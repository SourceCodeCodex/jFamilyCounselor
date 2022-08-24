package ro.lrg.jfamilycounselor.plugin;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IStartup;

import ro.lrg.jfamilycounselor.plugin.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;
import ro.lrg.jfamilycounselor.plugin.impl.MConcreteTypePair;
import ro.lrg.jfamilycounselor.plugin.impl.MProject;
import ro.lrg.jfamilycounselor.plugin.impl.MRefPair;
import ro.lrg.jfamilycounselor.plugin.impl.MType;

public final class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		ToolRegistration.getInstance().registerXEntityConverter(element -> {
			if (element instanceof IJavaProject project) {
				return Factory.getInstance().createMProject(MProject.asScala(project));
			}
			if (element instanceof IType type) {
				return Factory.getInstance().createMType(MType.asScala(type));
			}

			if (element instanceof MProject mProject) {
				return Factory.getInstance().createMProject(mProject);
			}
			if (element instanceof MType mType) {
				return Factory.getInstance().createMType(mType);
			}
			if (element instanceof MRefPair mRefPair) {
				return Factory.getInstance().createMRefPair(mRefPair);
			}
			if (element instanceof MConcreteTypePair mTypePair) {
				return Factory.getInstance().createMConcreteTypePair(mTypePair);
			}

			return null;
		});
	}

}
