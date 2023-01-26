package ro.lrg.jfamilycounselor.project.group;

import jfamilycounselor.metamodel.entity.MProject;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class RelevantClasses implements IRelationBuilder<MType, MProject> {

	public Group<MType> buildGroup(MProject mProject) {
		var group = new Group<MType>();
		mProject.getUnderlyingObject()
				.relevantClasses()
				.map(Factory.getInstance()::createMType)
				.foreach(mType -> group.add(mType));
		return group;
	}
	
}
