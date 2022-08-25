package ro.lrg.jfamilycounselor.project.group;

import jfamilycounselor.metamodel.entity.MProject;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class MightHideFamilialCorrelationsClasses implements IRelationBuilder<MType, MProject> {

	@Override
	public Group<MType> buildGroup(MProject mProject) {
		var group = new Group<MType>();

		for (ro.lrg.jfamilycounselor.core.MType mType : mProject.getUnderlyingObject()
				.mightHideFamilialCorrelationsClasses()) {
			group.add(Factory.getInstance().createMType(mType));
		}

		return group;
	}
}
