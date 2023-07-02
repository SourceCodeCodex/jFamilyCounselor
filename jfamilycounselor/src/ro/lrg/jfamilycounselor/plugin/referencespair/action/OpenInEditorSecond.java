package ro.lrg.jfamilycounselor.plugin.referencespair.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditorSecond implements IActionPerformer<Void, MReferencesPair, HListEmpty> {

	@Override
	public Void performAction(MReferencesPair mReferencesPair, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mReferencesPair.getUnderlyingObject()._2, true, true);
		} catch (PartInitException | JavaModelException e) {
		}
		return null;
	}

}
