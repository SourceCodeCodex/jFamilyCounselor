package ro.lrg.jfamilycounselor.reference.pair.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MReferenceVariablesPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditorSecond implements IActionPerformer<Void, MReferenceVariablesPair, HListEmpty> {

	public Void performAction(MReferenceVariablesPair mReferenceVariablesPair, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mReferenceVariablesPair.getUnderlyingObject()._2().underlyingJdtObject(), true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
