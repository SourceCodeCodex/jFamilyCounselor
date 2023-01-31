package ro.lrg.jfamilycounselor.plugin.project.group;

import jfamilycounselor.metamodel.entity.MProject;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.capability.specific.project.RelevantClassesCapability;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class RelevantClasses implements IRelationBuilder<MType, MProject> {

    public Group<MType> buildGroup(MProject mProject) {
	var group = new Group<MType>();
	RelevantClassesCapability.relevantClasses(mProject.getUnderlyingObject())
		.stream()
		.map(Factory.getInstance()::createMType)
		.forEach(mType -> group.add(mType));
	return group;
    }

}
