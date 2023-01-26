package ro.lrg.jfamilycounselor.type.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditor implements IActionPerformer<Void, MType, HListEmpty> {

	public Void performAction(MType mClass, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mClass.getUnderlyingObject().underlyingJdtObject(), true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
