package ro.lrg.jfamilycounselor.plugin.project.group;

import jfamilycounselor.metamodel.entity.MProject;
import jfamilycounselor.metamodel.entity.MType;
import jfamilycounselor.metamodel.factory.Factory;
import ro.lrg.jfamilycounselor.approach.relevance.typeparameter.RelevantTypesByTypeParametersUtil;
import ro.lrg.xcore.metametamodel.Group;
import ro.lrg.xcore.metametamodel.IRelationBuilder;
import ro.lrg.xcore.metametamodel.RelationBuilder;

@RelationBuilder
public class RelevantTypesByTypeParameters implements IRelationBuilder<MType, MProject> {

    @Override
    public Group<MType> buildGroup(MProject mProject) {
	var group = new Group<MType>();
	RelevantTypesByTypeParametersUtil.relevantTypes(mProject.getUnderlyingObject()).stream()
		.map(Factory.getInstance()::createMType)
		.forEach(mType -> group.add(mType));
	return group;
    }

}
