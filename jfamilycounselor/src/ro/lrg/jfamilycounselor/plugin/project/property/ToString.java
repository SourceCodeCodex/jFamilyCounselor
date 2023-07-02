package ro.lrg.jfamilycounselor.plugin.project.property;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MProject> {

	@Override
	public String compute(MProject mProject) {
		return mProject.getUnderlyingObject().getElementName();
	}

}
