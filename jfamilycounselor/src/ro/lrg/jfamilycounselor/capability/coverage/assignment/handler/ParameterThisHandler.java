package ro.lrg.jfamilycounselor.capability.coverage.assignment.handler;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.AssignemntsPair;
import ro.lrg.jfamilycounselor.capability.coverage.assignment.model.State;

public class ParameterThisHandler extends ReferencesPairHandler {
    private ThisParameterHandler thisParameterHandler = new ThisParameterHandler();

    public void handle(AssignemntsPair assignemntsPair, State state) {
	thisParameterHandler.handle(assignemntsPair.swap(), state);
    }

 
    protected boolean canHandle(IJavaElement assignedElement1, IJavaElement assignedElement2) {
	return assignedElement1 instanceof ILocalVariable && assignedElement2 instanceof IType;
    }

}
