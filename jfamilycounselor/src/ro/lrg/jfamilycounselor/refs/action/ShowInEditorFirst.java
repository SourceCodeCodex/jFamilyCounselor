package ro.lrg.jfamilycounselor.refs.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MRefPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class ShowInEditorFirst implements IActionPerformer<Void, MRefPair, HListEmpty> {

	@Override
	public Void performAction(MRefPair mRefPair, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mRefPair.getUnderlyingObject().jdtElement1(), true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
