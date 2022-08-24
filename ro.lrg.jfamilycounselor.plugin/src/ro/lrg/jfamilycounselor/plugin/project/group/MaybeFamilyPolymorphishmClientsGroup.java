package ro.lrg.jfamilycounselor.plugin.project.group;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MType;
import ro.lrg.jfamilycounselor.plugin.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class MaybeFamilyPolymorphishmClientsGroup implements IRelationBuilder<MType, MProject> {

	@Override
	public Group<MType> buildGroup(MProject mProject) {
		var group = new Group<MType>();

		for (ro.lrg.jfamilycounselor.plugin.impl.MType mType : mProject.getUnderlyingObject()
				.maybeFamilyPolymorphismClients()) {
			group.add(Factory.getInstance().createMType(mType));
		}

		return group;
	}
}
