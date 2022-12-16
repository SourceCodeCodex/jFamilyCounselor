package ro.lrg.jfamilycounselor.type.pair.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditorFirst implements IActionPerformer<Void, MTypesPair, HListEmpty> {

	public Void performAction(MTypesPair mTypesPair, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mTypesPair.getUnderlyingObject()._1().underlyingJdtObject(), true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}