package ro.lrg.jfamilycounselor.plugin.project.group;

import jfamilycounselorplugin.metamodel.entity.MProject;
import jfamilycounselorplugin.metamodel.entity.MType;
import jfamilycounselorplugin.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class MaybeFamilyPolymorphishmClientsGroup implements IRelationBuilder<MType, MProject> {

    @Override
    public Group<MType> buildGroup(MProject mProject) {
	var group = new Group<MType>();

	for (ro.lrg.jfamilycounselor.metamodel.scala.MType mType : mProject.getUnderlyingObject()
		.maybeFamilyPolymorphismClients()) {
	    group.add(Factory.getInstance().createMType(mType));
	}

	return group;
    }
}
