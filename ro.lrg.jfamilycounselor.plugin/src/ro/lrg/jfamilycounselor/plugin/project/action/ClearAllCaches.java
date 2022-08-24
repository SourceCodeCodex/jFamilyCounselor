package ro.lrg.jfamilycounselor.plugin.project.action;

import ro.lrg.jfamilycounselor.plugin.impl.cache.Cache;
import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MProject;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class ClearAllCaches implements IActionPerformer<Void, MProject, HListEmpty> {

	@Override
	public Void performAction(MProject mProject, HListEmpty hList) {
		Cache.clearAllCaches();
		return null;
	}

}