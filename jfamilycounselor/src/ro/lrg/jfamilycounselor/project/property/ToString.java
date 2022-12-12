package ro.lrg.jfamilycounselor.project.property;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.xcore.metametamodel.IPropertyComputer;
import ro.lrg.xcore.metametamodel.PropertyComputer;

@PropertyComputer
public class ToString implements IPropertyComputer<String, MProject> {

	public String compute(MProject mProject) {
		return mProject.getUnderlyingObject().toString();
	}

}
