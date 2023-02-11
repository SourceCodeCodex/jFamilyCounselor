package ro.lrg.jfamilycounselor.plugin.type.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MType;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditor implements IActionPerformer<Void, MType, HListEmpty> {

    public Void performAction(MType mType, HListEmpty hList) {
	try {
	    JavaUI.openInEditor(mType.getUnderlyingObject(), true, true);
	} catch (PartInitException | JavaModelException e) {
	}
	return null;
    }

}
