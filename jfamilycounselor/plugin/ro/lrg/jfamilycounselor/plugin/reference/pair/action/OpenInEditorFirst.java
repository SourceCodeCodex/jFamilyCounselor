package ro.lrg.jfamilycounselor.plugin.reference.pair.action;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MReferencesPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditorFirst implements IActionPerformer<Void, MReferencesPair, HListEmpty> {

    public Void performAction(MReferencesPair mReferencesPair, HListEmpty hList) {
	try {
	    JavaUI.openInEditor((IJavaElement) mReferencesPair.getUnderlyingObject()._1, true, true);
	} catch (PartInitException | JavaModelException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
