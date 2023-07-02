package ro.lrg.jfamilycounselor.plugin.typeparameterspair.action;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MTypeParametersPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditorSecond implements IActionPerformer<Void, MTypeParametersPair, HListEmpty> {

	@Override
	public Void performAction(MTypeParametersPair mTypeParametersPair, HListEmpty hList) {
		try {
			JavaUI.openInEditor(mTypeParametersPair.getUnderlyingObject()._2, true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
