package ro.lrg.jfamilycounselor;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IStartup;

import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.insider.view.ToolRegistration;
import ro.lrg.jfamilycounselor.core.model.project.Project;
import ro.lrg.jfamilycounselor.core.model.reference.pair.ReferenceVariablesPair;
import ro.lrg.jfamilycounselor.core.model.type.Type;
import ro.lrg.jfamilycounselor.core.model.type.pair.TypesPair;

public final class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		ToolRegistration.getInstance().registerXEntityConverter(element -> {
			if (element instanceof IJavaProject project) {
				return Factory.getInstance().createMProject(new Project(project));
			}
			if (element instanceof IType type) {
				return Factory.getInstance().createMType(new Type(type));
			}

			if (element instanceof Project mProject) {
				return Factory.getInstance().createMProject(mProject);
			}
			if (element instanceof Type mType) {
				return Factory.getInstance().createMType(mType);
			}
			if (element instanceof ReferenceVariablesPair mRefPair) {
				return Factory.getInstance().createMReferenceVariablesPair(mRefPair);
			}
			if (element instanceof TypesPair mTypePair) {
				return Factory.getInstance().createMTypesPair(mTypePair);
			}

			return null;
		});
	}

}
