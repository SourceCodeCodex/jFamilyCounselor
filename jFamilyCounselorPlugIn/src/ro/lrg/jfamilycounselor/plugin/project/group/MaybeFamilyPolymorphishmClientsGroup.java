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

	System.out.println(System.nanoTime());
	for (ro.lrg.jfamilycounselor.MType refPair : mProject.getUnderlyingObject().maybeFamilyPolymorphismClients()) {
	    group.add(Factory.getInstance().createMType(refPair));
	}
	System.out.println(System.nanoTime());

	return group;
    }
}
