package ro.lrg.jfamilycounselor.plugin.typespair.action;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;

import jfamilycounselor.metamodel.entity.MTypesPair;
import ro.lrg.xcore.metametamodel.ActionPerformer;
import ro.lrg.xcore.metametamodel.HListEmpty;
import ro.lrg.xcore.metametamodel.IActionPerformer;

@ActionPerformer
public final class OpenInEditorSecond implements IActionPerformer<Void, MTypesPair, HListEmpty> {

	@Override
	public Void performAction(MTypesPair mTypesPair, HListEmpty hList) {
		try {
			JavaUI.openInEditor((IType) mTypesPair.getUnderlyingObject()._2, true, true);
		} catch (PartInitException | JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

}
