package ro.lrg.jfamilycounselor.plugin.types.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import ro.lrg.jfamilycounselor.plugin.metamodel.entity.MConcreteTypePair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class ShowInEditorSecond implements IActionPerformer<Void, MConcreteTypePair, HListEmpty> {

	@Override
	public Void performAction(MConcreteTypePair mTypePair, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mTypePair.getUnderlyingObject().jdtElement2(), true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
