package ro.lrg.jfamilycounselor.plugin.type.pair.action;

import org.eclipse.jdt.core.IType;
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
	    JavaUI.openInEditor((IType) mTypesPair.getUnderlyingObject()._1, true, true);
	} catch (PartInitException | JavaModelException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
