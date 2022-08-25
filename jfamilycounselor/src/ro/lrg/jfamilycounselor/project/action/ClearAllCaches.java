package ro.lrg.jfamilycounselor.project.action;

import jfamilycounselor.metamodel.entity.MProject;
import ro.lrg.jfamilycounselor.core.cache.Cache;
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
