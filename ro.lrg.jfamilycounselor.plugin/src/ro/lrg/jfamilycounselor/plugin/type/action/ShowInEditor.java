package ro.lrg.jfamilycounselor.plugin.type.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class ShowInEditor implements IActionPerformer<Void, MType, HListEmpty> {

	@Override
	public Void performAction(MType mClass, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mClass.getUnderlyingObject().jdtElement(), true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
